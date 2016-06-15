/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.yeepay.gate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.guzhi.pay.channel.yeepay.YeePayConsts;
import com.guzhi.pay.channel.yeepay.YeePayHelper;
import com.guzhi.pay.channel.yeepay.card.AbstractYeePayAdapter;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 易宝网关支付
 * 参考文档《易宝支付产品(HTML版)通用接口文档 v3.0》
 * 
 * @author administrator
 */
@Component("yeepayGateAdapter")
public class YeePayGateAdapter extends AbstractYeePayAdapter {

    private static Logger LOG = LoggerFactory.getLogger(YeePayGateAdapter.class);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put(YeePayConsts.P0_CMD, YeePayConsts.BUY_CMD);
        reqParams.put(YeePayConsts.P1_MERID, payOrder.getAppChInfo().getChAccountId());
        reqParams.put(YeePayConsts.P2_ORDER, payOrder.getChOrderId());
        reqParams.put(YeePayConsts.P3_AMT, payOrder.getAmount().toString());
        reqParams.put(YeePayConsts.P4_CUR, YeePayConsts.CNY);
        // reqParams.put(YeePayConsts.P5_PID,
        // StringUtils.defaultString(payOrder.getProdId(), ""));
        // reqParams.put(YeePayConsts.P6_PCAT,
        // StringUtils.defaultString(payOrder.getProdName(), ""));
        // reqParams.put(YeePayConsts.P7_PDESC,
        // StringUtils.defaultString(payOrder.getProdDesc(), ""));
        // TODO We will set product information in English,to avoid a character
        // encoding bug situation.
        reqParams.put(YeePayConsts.P5_PID, "Duowan");
        reqParams.put(YeePayConsts.P6_PCAT, "Duowan");
        reqParams.put(YeePayConsts.P7_PDESC, "Duowan");

        reqParams.put(YeePayConsts.P8_URL, getguzhiPayNotify() + YeePayConsts.ADDR_YEEPAY_GATE_NOTIFY);
        reqParams.put(YeePayConsts.P9_SAF, YeePayConsts.DEFAULT_SAF);
        reqParams.put(YeePayConsts.PA_MP, "");
        if (StringUtils.isNotBlank(payOrder.getBankId())) {
            reqParams.put(YeePayConsts.PD_FRPID, payOrder.getBankId());
        } else {
            reqParams.put(YeePayConsts.PD_FRPID, "");
        }
        reqParams.put(YeePayConsts.PR_NEEDRESPONSE, YeePayConsts.NEED_RESPONSE);
        String hmac = YeePayHelper.getPaySign(reqParams, payOrder.getAppChInfo().getChPayKeyMd5());

        reqParams.put(YeePayConsts.HMAC, hmac);
        String payUrl = UrlHelper.addQuestionMark(YeePayConsts.YEEPAY_PAY_URL)
                + StringHelper.assembleResqStr(reqParams, YeePayConsts.GBK);
        LOG.info("[yeepayGateAdapter.pay] PayUrl created successfully,payurl:{}", payUrl);
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setStatusMsg("等待用户支付订单，或等待易宝通知");
        LOG.info("[gbGateAdapter.pay] return payOrder:{}", payOrder, "ds:trace:" + payOrder.getAppOrderId());
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        return super.query(payOrder);
        // Map<String, String> reqParams = new HashMap<String, String>();
        // reqParams.put(YeePayConsts.P0_CMD, YeePayConsts.QUERYORDERDETAIL);
        // reqParams.put(YeePayConsts.P1_MERID, payOrder.getChAccountId());
        // reqParams.put(YeePayConsts.P2_ORDER, payOrder.getChOrderId());
        // String hmac = DigestUtil.hamcSign(reqParams,
        // payOrder.getAppChInfo().getChPayKeyMd5());
        // reqParams.put(YeePayConsts.HMAC, hmac);
        // String queryUrl =
        // UrlHelper.addQuestionMark(YeePayConsts.YEEPAY_QUERY_URL)
        // + StringHelper.assembleResqStr(reqParams, YeePayConsts.GBK);
        // LOG.info("[yeepayGateAdapter.query] queryUrl created successfully,queryurl:{}",
        // queryUrl);
        // String respStr = HttpClientHelper.sendRequest(queryUrl,
        // YeePayConsts.GBK);
        // YeePayHelper.updatePayOrderByQuery(respStr, payOrder);
        // return payOrder;
    }

    public PayOrder refund(PayOrder payOrder) {
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put(YeePayConsts.P0_CMD, YeePayConsts.REFUND_CMD);
        reqParams.put(YeePayConsts.P1_MERID, YeePayConsts.YEEPAY_MERCHAND_ID);
        reqParams.put(YeePayConsts.PB_TRXID, payOrder.getChOrderId());
        // TODO Is the format method correct?
        reqParams.put(YeePayConsts.P3_AMT, new BigDecimal(payOrder.getRefundAmount()).setScale(2, RoundingMode.HALF_UP)
                .toString());

        reqParams.put(YeePayConsts.P4_CUR, YeePayConsts.CNY);
        // TODO According to the reference,the Chinese characters should be
        // encoded in "gbk".
        reqParams.put(YeePayConsts.P5_PID, payOrder.getRefundDesc());
        String hmac = YeePayHelper.getRefundSign(reqParams, payOrder.getAppChInfo().getChPayKeyMd5());
        reqParams.put(YeePayConsts.HMAC, hmac);
        String requestUrl = UrlHelper.addQuestionMark(YeePayConsts.YEEPAY_REFUND_URL)
                + StringHelper.assembleResqStr(reqParams);

        String resp = HttpClientHelper.sendRequest(requestUrl, YeePayConsts.GBK);
        return YeePayHelper.updatePayOrderByRefund(resp, payOrder);
    }

    @Override
    public String getPdFrpId() {
        return null;
    }

    @Override
    public void createPayTask(PayOrder payOrder) {
        // 易宝gate没有充值任务，为空方法体。
    }
}
