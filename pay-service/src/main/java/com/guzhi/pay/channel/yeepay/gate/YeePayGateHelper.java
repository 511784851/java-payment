/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.yeepay.gate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.channel.yeepay.YeePayConsts;
import com.guzhi.pay.channel.yeepay.util.DigestUtil;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.TraceHelper;

/**
 * 易宝网关支付帮助类。
 * 
 * @author 
 * 
 */
public class YeePayGateHelper {
    private static final Logger LOG = LoggerFactory.getLogger(YeePayGateHelper.class);

    /**
     * 校验Gate Notify报文的签名。
     * 
     * @param params
     * @return
     */
    public static String getNotifySign(Map<String, String> params, String key) {
        List<String> keyList = new ArrayList<String>(params.keySet());
        Collections.sort(keyList);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyList.size(); i++) {
            if (StringUtils.isAlpha(StringUtils.substring(keyList.get(i), 1, 2)))
                continue;
            sb.append(params.get(keyList.get(i)));
        }
        LOG.info("[getNotifySign] source string:{}", sb.toString());
        return DigestUtil.hmacSign(sb.toString(), key);
    }

    /**
     * 根据异步通知修改payorder状态。
     * 
     * @param resource
     * @param params
     * @return
     */
    public static PayOrder assemblePayOrderByNotify(DomainResource resource, Map<String, String> params) {
        String chOrderId = params.get(YeePayConsts.R6_ORDER);
        PayOrder payOrder = Help.getPayOrderByNotify(resource, chOrderId);
        String signInReq = params.remove(YeePayConsts.HMAC);
        String signExpect = getNotifySign(params, payOrder.getAppChInfo().getChPayKeyMd5());
        if (!signExpect.equals(signInReq)) {
            LOG.error("[assemblePayOrderByNotify] unmatched sign,orderId:{},signInReq:{},signExpect:{}", chOrderId,
                    signInReq, signExpect, TraceHelper.getTrace(payOrder));
            throw new PayException(Consts.SC.CHANNEL_ERROR, "unmatched sign.");
        }
        if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return payOrder;
        }
        // We will not update payorder status when receiving a return request.
        if (isReturnTypeRequest(params)) {
            return payOrder;
        }
        if (!YeePayConsts.NOTIFY_SUCCESS_CODE.equalsIgnoreCase(params.get(YeePayConsts.R1_CODE))) {
            payOrder.setStatusCode(Consts.SC.FAIL);
            payOrder.setStatusMsg("易宝：" + YeePayConsts.PAY_FAILED_MSG);
            LOG.error("[assemblePayOrderByNotify] set yeepay order fail,orderId:{},r1_code:{}.", chOrderId,
                    params.get(YeePayConsts.R1_CODE), TraceHelper.getTrace(payOrder));
            return payOrder;
        }
        if (new BigDecimal(params.get(YeePayConsts.R3_AMT)).compareTo(payOrder.getAmount()) != 0) {
            payOrder.setStatusCode(Consts.SC.CHANNEL_ERROR);
            payOrder.setStatusMsg("支付平台：金额不匹配，" + "请求金额：" + payOrder.getAmount().toPlainString() + "，响应金额："
                    + params.get(YeePayConsts.R3_AMT));
            LOG.error(
                    "[assemblePayOrderByNotify] unmatched amount error,orderId:{},expectedAmonut:{},requestAmount:{}",
                    chOrderId, payOrder.getAmount().toPlainString(), params.get(YeePayConsts.R3_AMT),
                    TraceHelper.getTrace(payOrder));
            return payOrder;
        }
        payOrder.setStatusCode(Consts.SC.SUCCESS);
        payOrder.setStatusMsg("易宝：" + YeePayConsts.PAY_SUCCESS_MSG);
        LOG.info("[assemblePayOrderByNotify] pay succcess.orderId:{}", chOrderId, TraceHelper.getTrace(payOrder));
        payOrder.setBankId(params.get(YeePayConsts.RO_BANKORDERID));
        payOrder.setChDealId(params.get(YeePayConsts.R2_TRXID));
        payOrder.setChDealTime(params.get(YeePayConsts.RP_PAYDATE));
        return payOrder;
    }

    /**
     * Check value of "r9_BType"
     * "1" means redirecting neccessary,as a normal return operation,return
     * true;
     * "2" means redirecting unneccessary,as a normal notify operation,return
     * false.
     * Other conditions are not excepted,return false.
     * 
     * @param params
     * @return
     */
    public static boolean isReturnTypeRequest(Map<String, String> params) {
        String r9_Btype = params.get(YeePayConsts.R9_BTYPE);
        if (YeePayConsts.RETURNTYPE.equalsIgnoreCase(r9_Btype)) {
            return true;
        } else if (YeePayConsts.NOTIFYTYPE.equalsIgnoreCase(r9_Btype)) {
            return false;
        } else {
            return false;
        }
    }
}