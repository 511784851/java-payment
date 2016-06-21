/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.jw;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * 骏网一卡通充值实现
 * 
 * @author administrator
 * 
 */
@Service("jwJkAdapter")
public class JwJkAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(JwJkAdapter.class);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        AppChInfo appChInfo = payOrder.getAppChInfo();
        payOrder.setChOrderId(getChOrderId(payOrder));
        Map<String, String> request = new HashMap<String, String>();
        request.put(JwConsts.KEY_AGENT_ID, appChInfo.getChAccountId());
        request.put(JwConsts.KEY_BILL_ID, payOrder.getChOrderId());
        request.put(JwConsts.KEY_BILL_TIME, payOrder.getSubmitTime());
        request.put(JwConsts.KEY_CARD_DATA, JwHelper.assembleCardData(payOrder));
        request.put(JwConsts.KEY_PAY_JPOINT, StringHelper.getAmount(payOrder.getAmount()));
        request.put(JwConsts.KEY_TIME_STAMP, TimeHelper.get(8, new Date()));
        // 日志中不能存在卡密码
        payOrder.setCardPass("");
        LOG.info("[JwJkAdapter.pay] with PayOrder:{}", payOrder, "ds:trace:" + payOrder.getAppOrderId());
        String signMsg = JwHelper.genPaySign(request, appChInfo.getChPayKeyMd5());
        request.put(JwConsts.KEY_SIGN, signMsg);
        request.put(JwConsts.KEY_NOTIFY_URL, UrlHelper.removeLastSep(getPayNotify()) + JwConsts.ADDR_YYPAY_NOTIFY);
        String payUrl = JwConsts.ADDR_JW_PAY + StringHelper.assembleResqStr(request);
        LOG.info("[JwJkAdapter.pay] with payUrl:{}", payUrl, "ds:trace:" + payOrder.getAppOrderId());
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setStatusMsg("等待骏网通知");
        // 异步充值
        payOrder.setAsyncPayTaskType(Task.TYPE_PAY_JWJK);
        // String respStr = HttpClientHelper.sendRequest(payUrl,
        // Consts.CHARSET_UTF8);
        // LOG.info("[JwJkAdapter.respStr] with respStr:{}", respStr,
        // "ds:trace:" + payOrder.getAppOrderId());
        // JwHelper.updatePayOrderByPay(payOrder, respStr,
        // appChInfo.getChPayKeyMd5());
        return payOrder;
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        LOG.info("[JwJkAdapter.query] with PayOrder:{}", payOrder, "ds:trace:" + payOrder.getAppOrderId());
        AppChInfo appChInfo = payOrder.getAppChInfo();
        Map<String, String> request = new HashMap<String, String>();
        request.put(JwConsts.KEY_AGENT_ID, appChInfo.getChAccountId());
        request.put(JwConsts.KEY_BILL_ID, payOrder.getChOrderId());
        request.put(JwConsts.KEY_TIME_STAMP, TimeHelper.get(8, new Date()));
        String signMsg = JwHelper.genQuerySign(request, appChInfo.getChPayKeyMd5());
        request.put(JwConsts.KEY_SIGN, signMsg);
        String queryUrl = JwConsts.ADDR_JW_QUERY + StringHelper.assembleResqStr(request);
        LOG.info("[JwJkAdapter.query] with queryUrl:{}", queryUrl, "ds:trace:" + payOrder.getAppOrderId());
        String respStr = HttpClientHelper.sendRequest(queryUrl, Consts.CHARSET_UTF8);
        LOG.info("[JwJkAdapter.respStr] with respStr:{}", respStr, "ds:trace:" + payOrder.getAppOrderId());
        JwHelper.updatePayOrderByQuery(payOrder, respStr);
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

}
