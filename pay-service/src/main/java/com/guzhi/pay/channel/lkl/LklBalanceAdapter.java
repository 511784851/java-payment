/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.lkl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * @author administrator
 * 
 */
@Service("lklBalanceAdapter")
public class LklBalanceAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(LklBalanceAdapter.class);
    private static final String VERSION = "20060301";
    private static final DecimalFormat decimalFormat = new DecimalFormat("0");
    // 0：无验签；1：数字签名；2：MD5
    private static final String MACTYPE = "2";
    @Value("${lklBalancePayUrl}")
    private String lklBalancePayUrl;

    @Value("${lklBalanceQueryUrl}")
    private String lklBalanceQueryUrl;

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        LOG.info("[LklBalanceAdapter.pay] with PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> request = new HashMap<String, String>();
        request.put(LklConsts.VER, VERSION);
        request.put(LklConsts.MERID, payOrder.getAppChInfo().getChAccountId());
        request.put(LklConsts.MINCODE,
                JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), LklConsts.MINCODE));
        request.put(LklConsts.ORDERID, payOrder.getChOrderId());
        // 金额单位是分，不能有小数
        request.put(LklConsts.AMOUNT, decimalFormat.format(payOrder.getAmount().doubleValue() * 100));
        request.put(LklConsts.RANDNUM, LklHelper.getRandom());
        // 即是通知回调地址
        request.put(LklConsts.PAYURL, UrlHelper.removeLastSep(getguzhiPayNotify()) + LklConsts.ADDR_guzhiPay_NOTIFY);
        request.put(LklConsts.PRODUCTNAME, payOrder.getProdName());
        request.put(LklConsts.DESC, payOrder.getProdDesc());
        // 账单失效期如果不设置默认为一天，时间单位是分钟
        request.put(LklConsts.EXPIREDTIME, "");
        request.put(LklConsts.MACTYPE, MACTYPE);
        String signMsg = LklHelper.getPaySign(request, payOrder);
        request.put(LklConsts.MAC, signMsg);
        request.remove(LklConsts.PAYURL);
        String payUrl = UrlHelper.addQuestionMark(lklBalancePayUrl) + StringHelper.assembleResqStr(request);
        LOG.info("[LklBalanceAdapter.pay] with payUrl:{}", payUrl, TraceHelper.getTrace(payOrder));
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.appendMsg("PayUrl created successfully");
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        Map<String, String> request = new HashMap<String, String>();
        request.put(LklConsts.VER_ID, VERSION);
        request.put(LklConsts.MER_ID, payOrder.getAppChInfo().getChAccountId());
        request.put(LklConsts.ORDER_ID, payOrder.getChOrderId());
        request.put(LklConsts.ORDER_DATE, payOrder.getSubmitTime() + "");
        request.put(LklConsts.MAC_TYPE, MACTYPE);
        String signMsg = LklHelper.getQuerySign(request, payOrder);
        request.put(LklConsts.VERIFY_STRING, signMsg);
        String queryUrl = UrlHelper.addQuestionMark(lklBalanceQueryUrl) + StringHelper.assembleResqStr(request);
        LOG.info("[LklBalanceAdapter.query] with queryUrl:{}", queryUrl, TraceHelper.getTrace(payOrder));
        String queryRespStr = HttpClientHelper.sendRequest(queryUrl, Consts.CHARSET_UTF8);
        LOG.info("[LklBalanceAdapter.query] with queryRespStr:{}", queryRespStr, TraceHelper.getTrace(payOrder));
        LklHelper.updateByQueryResult(queryRespStr, payOrder);
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

}
