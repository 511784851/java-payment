/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.apple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.AbstractChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;

/**
 * @author administrator
 *         苹果凭证(类似支付宝余额)充值
 */
@Service("appleBalanceAdapter")
public class AppleBalanceAdapter extends AbstractChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(AppleBalanceAdapter.class);

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        LOG.info("[AppleBalanceAdapter.pay] with PayOrder:{}", payOrder, "ds:trace:" + payOrder.getAppOrderId());
        String payUrl = AppleBalanceHelper.assembleProdUrl(payOrder);
        payOrder.setPayUrl(payUrl);
        payOrder.setStatusCode(Consts.SC.PENDING);
        payOrder.setStatusMsg("payorder created successfully");
        return payOrder;
    }

    /**
     * 苹果充值没有查询接口
     */
    @Override
    public PayOrder query(PayOrder payOrder) {
        return payOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("not implemented yet");
    }

}
