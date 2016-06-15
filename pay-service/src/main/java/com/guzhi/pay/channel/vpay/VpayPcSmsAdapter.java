/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.vpay;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * 盈华讯方PC短信支付
 * 参考文档《深圳盈华讯方移动短信支付商户WEB接口规范(WEB版).pdf》
 * 
 * @author 
 * 
 */
@Service("vpayPcsmsAdapter")
public class VpayPcSmsAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(VpayPcSmsAdapter.class);

    @Override
    public String status() {
        throw new PayException(Consts.SC.REQ_ERROR, "Operation status is not supported by VpayPcSms.");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> reqParams = new LinkedHashMap<String, String>();
        reqParams.put(VpayConsts.KEY_SP, payOrder.getAppChInfo().getChAccountId());
        reqParams.put(VpayConsts.KEY_OD, payOrder.getChOrderId());
        if (payOrder.getAmount().compareTo(new BigDecimal(payOrder.getAmount().intValue())) != 0) {
            throw new PayException(Consts.SC.DATA_ERROR, "支付平台：盈华讯方不支持非整数面额");
        }
        reqParams.put(VpayConsts.KEY_MZ, VpayHelper.generateIntAmountString(payOrder.getAmount()));
        reqParams.put(VpayConsts.KEY_SPZDY, VpayConsts.SPZDY);
        reqParams.put(VpayConsts.KEY_UID, JsonHelper.fromJson(payOrder.getUserId(), Consts.gbUID));
        // spreq为盈华讯方支付首页，返回商户网站的链接地址
        String spreq = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), VpayConsts.KEY_PROD_URL);
        if (StringUtils.isBlank(spreq)) {
            spreq = VpayConsts.DEFAULT_SP_REQ;
        }
        reqParams.put(VpayConsts.KEY_SP_REQ, spreq);
        // 当前的spsuc参数无效，前台通知和后台通知统一使用盈华讯方工作人员配置的通知地址。
        reqParams.put(VpayConsts.KEY_SPSUC, getguzhiPayNotify() + VpayConsts.PC_NOTIFY_URL);
        String requestUrl = VpayHelper.generatePcSmsPayUrl(reqParams, payOrder);
        payOrder.setPayUrl(requestUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setStatusMsg("等待用户支付，或等待盈华讯方通知");
        LOG.info("generate pay url success for vpay pc sms,gbuid:{},orderid:{},payUrl:{}.",
                reqParams.get(VpayConsts.KEY_UID), payOrder.getChOrderId(), requestUrl, TraceHelper.getTrace(payOrder));
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new PayException(Consts.SC.REQ_ERROR, "Operation refunding is not supported by VpayPcSms.");
    }
}
