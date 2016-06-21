/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.kq.card;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.channel.kq.KqConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.TraceHelper;

/**
 * 快钱盛大支付方式
 * 
 * @author administrator
 * 
 */
@Service("kqSdAdapter")
public class KqSdAdapter extends KqCardAdapter implements ChannelIF {

    private static final Logger LOG = LoggerFactory.getLogger(KqSdAdapter.class);

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void assembleCardInfo(Map<String, String> request, PayOrder order) {
        return; // just to do nothing because of the payType
    }

    @Override
    public String getBossType() {
        return KqConsts.KQ_SD_BOSS_TYPE;
    }

    @Override
    public String getPayType() {
        return KqConsts.KQ_SD_CARD_PAY_TYPE;
    }

    @Override
    public void updatePayOrder(String payUrl, PayOrder order) {
        LOG.info("[KqSdAdapter.updatePayOrder] with payUrl: {}", payUrl, TraceHelper.getTrace(order));
        order.setPayUrl(payUrl);
        order.setStatusCode(Consts.SC.PENDING);
        order.setStatusMsg("等待用户支付订单，或等待快钱通知");
        LOG.info("[KqSdAdapter.updatePayOrder] return PayOrder: {}", order, TraceHelper.getTrace(order));
    }

    @Override
    public String getAmountFlag() {
        return KqConsts.KQ_FULL_AMOUNT_FLAG_FALSE;
    }

}
