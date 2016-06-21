/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.tenpay;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.UrlHelper;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 财付通.
 * 
 * @author administrator
 * 
 */
public abstract class AbstractTenpayAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTenpayAdapter.class);

    @Override
    public String status() {
        return null;
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put(TenpayConsts.KEY_SIGN_TYPE, "MD5");
        dataMap.put(TenpayConsts.KEY_SERVICE_VERSION, "1.0");
        dataMap.put(TenpayConsts.KEY_INPUT_CHARSET, "UTF-8");
        dataMap.put(TenpayConsts.KEY_SIGN_KEY_INDEX, "1");
        dataMap.put(TenpayConsts.KEY_BANK_TYPE, getBankId(payOrder));
        dataMap.put(TenpayConsts.KEY_BODY, payOrder.getProdName());
        dataMap.put(TenpayConsts.KEY_RETURN_URL, UrlHelper.removeLastSep(getPayNotify())
                + TenpayConsts.ADDR_YYPAY_RETURN);
        dataMap.put(TenpayConsts.KEY_NOTIFY_URL, UrlHelper.removeLastSep(getPayNotify())
                + TenpayConsts.ADDR_YYPAY_NOTIFY);
        dataMap.put(TenpayConsts.KEY_PARTNER, payOrder.getChAccountId());
        dataMap.put(TenpayConsts.KEY_OUT_TRADE_NO, payOrder.getChOrderId());
        // 金额参数单位为分
        dataMap.put(TenpayConsts.KEY_TOTAL_FEE, StringHelper.getAmount(payOrder.getAmount()));
        dataMap.put(TenpayConsts.KEY_FEE_TYPE, "1");
        dataMap.put(TenpayConsts.KEY_SPBILL_CREATE_IP, payOrder.getUserIp());
        dataMap.put(TenpayConsts.KEY_SIGN, TenpayHelper.generateSign(dataMap, payOrder.getAppChInfo().getChPayKeyMd5()));
        String payUrl = TenpayConsts.ADDR_TENPAYGATE_PAY + "?"
                + StringHelper.assembleResqStr(dataMap, Consts.CHARSET_UTF8);
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setStatusMsg("等待用户支付，或财付通通知");
        LOG.info("[pay] generate tenpay pay url success,payOrder:{}", payOrder);
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put(TenpayConsts.KEY_SIGN_TYPE, "MD5");
        dataMap.put(TenpayConsts.KEY_SERVICE_VERSION, "1.0");
        dataMap.put(TenpayConsts.KEY_INPUT_CHARSET, "UTF-8");
        dataMap.put(TenpayConsts.KEY_SIGN_KEY_INDEX, "1");
        dataMap.put(TenpayConsts.KEY_PARTNER, payOrder.getChAccountId());
        dataMap.put(TenpayConsts.KEY_OUT_TRADE_NO, payOrder.getChOrderId());
        dataMap.put(TenpayConsts.KEY_SIGN, TenpayHelper.generateSign(dataMap, payOrder.getAppChInfo().getChPayKeyMd5()));
        String queryUrl = TenpayConsts.ADDR_TENPAYGATE_QUERY + "?"
                + StringHelper.assembleResqStr(dataMap, Consts.CHARSET_UTF8);
        String responseString = HttpClientHelper.sendRequest(queryUrl);
        TenpayHelper.updatePayOrderByQuery(payOrder, responseString);
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new NotImplementedException();
    }

    public abstract String getBankId(PayOrder payOrder);
}