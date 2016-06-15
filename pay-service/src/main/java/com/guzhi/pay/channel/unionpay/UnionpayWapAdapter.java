/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.unionpay;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.HttpRetryHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * @author 
 * 
 */
@Service("unionpayWapAdapter")
public class UnionpayWapAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(UnionpayWapAdapter.class);

    @Value("${unionpay_cer_file_path}")
    private String publicKeyFilePath;

    @Value("${unionpay_pfx_file_path}")
    private String privateKeyFilePath;

    protected static String PUBLICKEYFILEPATH;
    protected static String PRIVATEKEYFILEPATH;

    @Value("${unionpay_gate_url}")
    private String gateUrl;

    @Override
    public String status() {
        return null;
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        LOG.info("[UnionpayWapAdapter.pay] with PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> frontUrlParamMap = new HashMap<String, String>();
        frontUrlParamMap.put(Constants.KEY_MERCHANTORDERID, payOrder.getChOrderId());
        String frontUrl = UnionpayWapHelper.generateFrontUrl(payOrder, getguzhiPayNotify());
        Map<String, String> request = new LinkedHashMap<String, String>();
        request.put(Constants.KEY_MERCHANTID, payOrder.getAppChInfo().getChAccountId());
        request.put(Constants.KEY_MERCHANTORDERID, payOrder.getChOrderId());
        request.put(Constants.KEY_MERCHANTORDERTIME, TimeHelper.get(8, new Date()));
        request.put(Constants.KEY_MERCHANTORDERAMT, UnionpayWapHelper.generateCentAmountString(payOrder.getAmount()));
        request.put(Constants.KEY_MERCHANTORDERCURRENCY, Constants.MERCHANT_ORDER_CURRENCY);
        if (StringUtils.isNotBlank(payOrder.getProdName())) {
            request.put(Constants.KEY_MERCHANTORDERDESC, payOrder.getProdName());
        }
        request.put(Constants.KEY_TRANSTYPE, Constants.TRANSTYPE);
        request.put(Constants.KEY_GWTYPE, Constants.GWTYPE);
        request.put(Constants.KEY_FRONTURL, frontUrl);
        request.put(Constants.KEY_BACKURL, UrlHelper.removeLastSep(getguzhiPayNotify()) + Constants.NOTIFYURL);
        String requestBody = UnionpayWapHelper.generatePayRequestBody(payOrder, request);

        long startTime = System.currentTimeMillis();
        String responseBody = HttpRetryHelper.sendRequest(gateUrl, requestBody, 300);
        LOG.info("[UnionpayWapAdapter.pay] time:{}, responseBody:{}", System.currentTimeMillis() - startTime,
                responseBody);
        UnionpayWapHelper.updatePayOrderWithPayResponse(responseBody, payOrder);
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        LOG.info("[UnionpayWapAdapter.query] with PayOrder:{}", payOrder, TraceHelper.getTrace(payOrder));
        String requestBody = UnionpayWapHelper.generateQueryRequestBody(payOrder);
        String responseBody = HttpClientHelper.sendRequest(gateUrl, requestBody, Consts.CHARSET_UTF8,
                Consts.CHARSET_UTF8);
        LOG.info("[UnionpayWapAdapter.query] with responseBody:{}", responseBody, TraceHelper.getTrace(payOrder));
        UnionpayWapHelper.updatePayOrderWithQueryResponse(responseBody, payOrder);
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        return null;
    }

    @PostConstruct
    public void init() {
        PRIVATEKEYFILEPATH = this.privateKeyFilePath;
        PUBLICKEYFILEPATH = this.publicKeyFilePath;
    }
}
