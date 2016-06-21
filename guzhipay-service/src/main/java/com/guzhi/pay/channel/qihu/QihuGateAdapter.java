/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.qihu;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.channel.zfb.ZfbConsts;
import com.guzhi.pay.channel.zfb.ZfbHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 奇虎的网关支付
 * 
 * @author administrator
 * 
 */
@Service("qihuGateAdapter")
public class QihuGateAdapter extends AbstractChannelIF {

    private static final Logger LOG = LoggerFactory.getLogger(QihuGateAdapter.class);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder order) {
        LOG.info("[QihuGateAdapter.pay] start paying.payOrder:{}", order, TraceHelper.getTrace(order));
        AppChInfo appChInfo = order.getAppChInfo();
        order.setChOrderId(getChOrderId(order));
        // assemble pay info
        Map<String, String> request = new HashMap<String, String>();
        request.put(QihuConsts.KEY_MER_CODE, appChInfo.getChAccountId());
        request.put(QihuConsts.KEY_MER_TRADE_CODE, order.getChOrderId());
        request.put(QihuConsts.KEY_TRANS_SERVICE, QihuConsts.VAlUE_TRANS_SERVICE);
        request.put(QihuConsts.KEY_INPUT_CHA, QihuConsts.CHARSET_UTF8);
        request.put(QihuConsts.KEY_SIGN_TYPE, QihuConsts.VALUE_SIGN_TYPE);
        request.put(QihuConsts.KEY_NOTIFY_URL, UrlHelper.removeLastSep(getPayNotify()) + QihuConsts.ADDR_YYPAY_NOTIFY);
        request.put(QihuConsts.KEY_RETURN_URL, UrlHelper.removeLastSep(getPayNotify()) + QihuConsts.ADDR_YYPAY_RETURN);
        request.put(QihuConsts.KEY_PRODUCT_NAME, order.getProdName());
        request.put(QihuConsts.KEY_REC_AMOUNT, String.valueOf(order.getAmount().doubleValue()));
        request.put(QihuConsts.KEY_PRODUCT_DESCT, order.getProdDesc());
        request.put(QihuConsts.KEY_CLIENT_IP, order.getUserIp());
        request.put(QihuConsts.KEY_TIMEOUT_SET, QihuConsts.PAY_TIMEOUT_SET);
        if (StringUtils.isNotBlank(order.getBankId())) {
            request.put(QihuConsts.KEY_BANK_CODE, order.getBankId());
        }
        request.put(QihuConsts.KEY_MER_ORDER_TIME, order.getSubmitTime());
        // gen sign
        String signMsg = QihuHelper.genSign(request, appChInfo.getChPayKeyMd5());
        request.put(ZfbConsts.KEY_SIGN, signMsg);
        // gen payUrl
        String payUrl = UrlHelper.removeLastSep(QihuConsts.ADDR_GATEWAY_PAY) + "?"
                + ZfbHelper.assembleQueryStr(request);
        order.setPayUrl(payUrl);
        order.setStatusCode(Consts.SC.PENDING);
        order.appendMsg("等待用户支付，或等待奇虎通知");
        LOG.info("[QihuGateAdapter.pay] create pay url successfully.payOrder: {}", order, TraceHelper.getTrace(order));
        return order;
    }

    @Override
    public PayOrder query(PayOrder order) {
        LOG.info("[QihuGateAdapter.query] start querying payOrder:{}", order, TraceHelper.getTrace(order));
        // basic info
        AppChInfo appChInfo = order.getAppChInfo();
        String md5Key = appChInfo.getChPayKeyMd5();
        // assemble query
        Map<String, String> request = new HashMap<String, String>();
        request.put(QihuConsts.KEY_MER_CODE, appChInfo.getChAccountId());
        request.put(QihuConsts.KEY_MER_TRADE_CODE, order.getChOrderId());
        request.put(QihuConsts.KEY_SIGN_TYPE, QihuConsts.VALUE_SIGN_TYPE);
        request.put(QihuConsts.KEY_OUT_FORMAT, QihuConsts.FORMAT_XML);
        // sign
        String signMsg = QihuHelper.genSign(request, md5Key);
        request.put(QihuConsts.KEY_SIGN, signMsg);
        // 构造查询地址
        String reqUrl = UrlHelper.removeLastSep(QihuConsts.ADDR_GATEWAY_QUERY) + "?"
                + ZfbHelper.assembleQueryStr(request);
        String respStr = HttpClientHelper.sendRequest(reqUrl, QihuConsts.CHARSET_UTF8);
        // 更新结果
        QihuHelper.updatePayOrderByQuery(order, respStr);
        return order;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

}
