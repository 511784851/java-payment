/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.vpay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 盈华讯方电话支付
 * 参考《V币统一电话支付平台技术文档11-19.doc》
 * 
 * @author administrator
 * @author update by administrator
 * 
 */
@Service("vpayTelAdapter")
public class VpayTelAdapter extends AbstractChannelIF {

    private static final Logger LOG = LoggerFactory.getLogger(VpayTelAdapter.class);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder order) {
        LOG.info("[VpayTelAdapter.pay] start paying.payOrder:{}", order, TraceHelper.getTrace(order));
        if (order.getAmount().compareTo(new BigDecimal(order.getAmount().intValue())) != 0) {
            throw new PayException(Consts.SC.DATA_ERROR, "支付平台：该渠道不支持非整数金额");
        }
        AppChInfo appChInfo = order.getAppChInfo();
        order.setChOrderId(getChOrderId(order));
        String spId = appChInfo.getChAccountId();
        String spname = appChInfo.getChAccountName();
        String yyuid = JsonHelper.fromJson(order.getUserId(), VpayConsts.KEY_YYUID);
        // 出现在电话钱包支付首页，返回商户，重新下单处。
        String spreq = JsonHelper.fromJson(order.getProdAddiInfo(), VpayConsts.KEY_PROD_URL);
        if (StringUtils.isEmpty(spreq)) {
            spreq = VpayConsts.DEFAULT_SP_REQ;
        }
        // assemble pay info
        Map<String, String> request = new HashMap<String, String>();
        request.put(VpayConsts.KEY_URLCODE, Consts.CHARSET_UTF8);
        request.put(VpayConsts.KEY_SP_VERSION, VpayConsts.SP_VERSION);
        request.put(VpayConsts.KEY_SP_ID, spId);
        request.put(VpayConsts.KEY_SP_NAME, spname);
        request.put(VpayConsts.KEY_SP_ORDERID, order.getChOrderId());
        request.put(VpayConsts.KEY_USER_ID, yyuid);
        request.put(VpayConsts.KEY_USER_IP, order.getUserIp());
        request.put(VpayConsts.KEY_SP_CUSTOM, "custom");
        // money只能整数
        request.put(VpayConsts.KEY_MONEY, String.valueOf(order.getAmount().intValue()));
        request.put(VpayConsts.KEY_SP_REQ, spreq);
        request.put(VpayConsts.KEY_NOTIFY_URL, UrlHelper.removeLastSep(getPayNotify()) + VpayConsts.VB_NOTIFY);
        // gen sign
        String signMsg = VpayHelper.genSignForTel(request, appChInfo.getChPayKeyMd5());
        request.put(VpayConsts.KEY_SP_MD5, signMsg);
        // gen payUrl
        String payUrl = UrlHelper.removeLastSep(getPayNotify() + VpayConsts.VPAYTEL_FRONT) + "?"
                + StringHelper.assembleResqStr(request);
        LOG.info("[VpayTelAdapter.pay] create pay url payUrl: {}", payUrl, TraceHelper.getTrace(order));
        order.setPayUrl(payUrl);
        order.setStatusCode(Consts.SC.PENDING);
        order.setStatusMsg("等待用户支付订单，或等待盈华讯方通知");

        LOG.info("[VpayTelAdapter.pay] create pay url successfully.payOrder: {}", order, TraceHelper.getTrace(order));
        return order;
    }

    @Override
    public PayOrder query(PayOrder order) {
        LOG.info("[VpayTelAdapter.query] start not need to do angthing:{}", order, TraceHelper.getTrace(order));
        return order;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

}
