/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.vpay;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * 大额短信支付.
 * 参考《APP版本深圳盈华讯方移动短信支付商户WEB接口规范(WEB版).doc》
 * 
 * @author administrator
 * 
 */
@Service("vpaySmsAdapter")
public class VpaySmsAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(VpaySmsAdapter.class);

    @Override
    public String status() {
        return null;
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> requestMap = new HashMap<String, String>();
        String mob = JsonHelper.fromJson(payOrder.getUserContact(), Consts.TEL);
        requestMap.put(VpayConsts.KEY_SP, payOrder.getAppChInfo().getChAccountId());
        requestMap.put(VpayConsts.KEY_OD, payOrder.getChOrderId());
        requestMap.put(VpayConsts.KEY_MZ, VpayHelper.generateIntAmountString(payOrder.getAmount()));
        requestMap.put(VpayConsts.KEY_SPREQ, VpayConsts.SPREQ);
        requestMap.put(VpayConsts.KEY_SPSUC, VpayConsts.SPSUC);
        requestMap.put(VpayConsts.KEY_SPZDY, VpayConsts.SPZDY);
        requestMap.put(VpayConsts.KEY_MOB, mob);
        String yyuid = JsonHelper.fromJson(payOrder.getUserId(), Consts.YYUID);
        requestMap.put(VpayConsts.KEY_UID, yyuid);
        String requestUrl = VpayHelper.generateSmsPayRequestUrl(requestMap, payOrder);
        LOG.info("[VpaySmsAdapter pay] generate pay request url success,url:{},payOrder:{}", requestUrl, payOrder,
                TraceHelper.getTrace(payOrder));
        String response = HttpClientHelper.sendRequest(requestUrl);
        LOG.info("[VpaySmsAdapter pay] generate pay response {},payOrder :{}", response, payOrder,
                TraceHelper.getTrace(payOrder));
        VpayHelper.updateSmsPayOrderWithPayResponse(response, payOrder);
        LOG.info("[VpaySmsAdapter pay] update payorder with response success,sending message as payurl,payOrder:{}",
                payOrder, TraceHelper.getTrace(payOrder));
        return payOrder;
    }

    // This channel has no interface for querying operation.
    @Override
    public PayOrder query(PayOrder payOrder) {
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        return null;
    }
}
