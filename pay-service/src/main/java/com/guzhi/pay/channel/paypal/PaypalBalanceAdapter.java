/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.paypal;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;
import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * @author Administrator
 *         paypal余额支付
 */
@Service("paypalBalanceAdapter")
public class PaypalBalanceAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(PaypalBalanceAdapter.class);
    private static final String VERSION = "98.0";
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Value("${addr_paypal_purvies}")
    private String addr_paypal_purvies;

    @Value("${addr_paypal_pay}")
    private String addr_paypal_pay;

    protected static String ADDR_PAYPAL_PURVIES;

    protected static String ADDR_PAYPAL_PAY;

    protected static String ADDR_PAYPAL_NOTIFYURL;

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @PostConstruct
    public void init() {
        ADDR_PAYPAL_PURVIES = this.addr_paypal_purvies;
        ADDR_PAYPAL_PAY = this.addr_paypal_pay;
        ADDR_PAYPAL_NOTIFYURL = UrlHelper.removeLastSep(getguzhiPayNotify()) + PaypalConsts.ADDR_guzhiPay_PAYPALNOTIFY;
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        Map<String, String> validatorMap = PaypalHelper.validateTransValidate(payOrder);
        String flag = validatorMap.get(TradeRuleConsts.FLAG);
        if (TradeRuleConsts.TRUE.equalsIgnoreCase(flag)) {
            payOrder.setStatusMsg("[paypal.validatePaypalForPay] exist risk, errorcode="
                    + validatorMap.get(TradeRuleConsts.TRADE_ERROR_CODE));
            LOG.warn("[PaypalBalanceAdapter.pay] validatePaypalForPay fail payorder:{},errorcode:{}", payOrder,
                    validatorMap.get(TradeRuleConsts.TRADE_ERROR_CODE), TraceHelper.getTrace(payOrder));
            // LOG.warn("[PaypalBalanceAdapter.pay] validatePaypalForPay fail,{}",
            // payOrder, "ds:alarm:paypal存在风控");
            throw new PayException(Consts.SC.RISK_ERROR, "pay exist risk ,errorcode="
                    + validatorMap.get(TradeRuleConsts.TRADE_ERROR_CODE));
        }
        Map<String, String> request = new HashMap<String, String>();
        payOrder.setChOrderId(getChOrderId(payOrder));
        request.put(PaypalConsts.USER, payOrder.getAppChInfo().getChAccountId());
        request.put(PaypalConsts.PWD, payOrder.getAppChInfo().getChPayKeyMd5());
        request.put(PaypalConsts.SIGNATURE,
                JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), PaypalConsts.APPCHINFO_SIGNATURE));
        request.put(PaypalConsts.VERSION, VERSION);
        request.put(PaypalConsts.PAYMENTREQUEST_0_PAYMENTACTION, PaypalConsts.PAYMENTACTION_SALE);
        request.put(PaypalConsts.METHOD, PaypalConsts.METHOD_SETEXPRESSCHECKOUT);
        request.put(PaypalConsts.PAYMENTREQUEST_0_AMT, decimalFormat.format(payOrder.getAmount()));
        request.put(PaypalConsts.SOLUTIONTYPE, PaypalConsts.SOLUTIONTYPEVALUE);
        request.put(PaypalConsts.LANDINGPAGE, PaypalConsts.LANDINGPAGEVALUE);
        request.put(PaypalConsts.RETURNURL, UrlHelper.removeLastSep(getguzhiPayNotify())
                + PaypalConsts.ADDR_guzhiPay_PAYPALTOKEN);
        request.put(PaypalConsts.CANCELURL, UrlHelper.removeLastSep(getguzhiPayNotify())
                + PaypalConsts.ADDR_guzhiPay_PAYPALCANCEL);
        request.put(PaypalConsts.PAYMENTREQUEST_0_NOTIFYURL, UrlHelper.removeLastSep(getguzhiPayNotify())
                + PaypalConsts.ADDR_guzhiPay_PAYPALNOTIFY);
        String purviewUrl = ADDR_PAYPAL_PURVIES + StringHelper.assembleResqStr(request);
        LOG.info("[PaypalBalanceAdapter.pay] with purviewUrl:{}", purviewUrl, TraceHelper.getTrace(payOrder));
        String respStr = HttpClientHelper.sendRequest(purviewUrl, Consts.CHARSET_UTF8);
        String decodeRespStr = StringHelper.decode(respStr, Consts.CHARSET_UTF8);
        PaypalHelper.updatePayOrderByPay(payOrder, decodeRespStr);
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        Map<String, String> request = new HashMap<String, String>();
        request.put(PaypalConsts.USER, payOrder.getAppChInfo().getChAccountId());
        request.put(PaypalConsts.PWD, payOrder.getAppChInfo().getChPayKeyMd5());
        request.put(PaypalConsts.SIGNATURE,
                JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), PaypalConsts.APPCHINFO_SIGNATURE));
        request.put(PaypalConsts.VERSION, VERSION);
        request.put(PaypalConsts.METHOD, PaypalConsts.METHOD_GETTRANSACTIONDETAILS);
        request.put(PaypalConsts.TRANSACTIONID, payOrder.getChDealId());
        String queryUrl = ADDR_PAYPAL_PURVIES + StringHelper.assembleResqStr(request);
        LOG.info("[PaypalBalanceAdapter.pay] with queryUrl:{}", queryUrl, TraceHelper.getTrace(payOrder));
        String respStr = HttpClientHelper.sendRequest(queryUrl, Consts.CHARSET_UTF8);
        PaypalHelper.updatePayOrderByQuery(payOrder, respStr);
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

}
