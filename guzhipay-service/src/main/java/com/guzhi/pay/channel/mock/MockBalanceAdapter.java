/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.StringHelper;

/**
 * mock 模拟的支付渠道
 * 
 * @author administrator
 * 
 */
@Service("mockBalanceAdapter")
public class MockBalanceAdapter implements ChannelIF {
    private static final Logger LOG = LoggerFactory.getLogger(MockBalanceAdapter.class);

    @Autowired
    private DomainResource resource;

    @Override
    public String status() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public PayOrder pay(PayOrder order) {
        order.setPayUrl("https://payplf-tpay-test.yy.com/pay-mock.html?"
                + StringHelper.encode(order.getAppId() + "@-@" + order.getAppOrderId() + "@-@" + order.getProdName()
                        + "@-@" + order.getAmount(), Consts.CHARSET_UTF8));
        order.setStatusCode(Consts.SC.PENDING);
        order.setStatusMsg("等待支付");
        LOG.info("[MockAdapter.pay] start paying.payOrder:{}", order);
        return order;
    }

    @Override
    public PayOrder query(PayOrder order) {
        PayOrder oriOrder = resource.getPayOrder(order.getAppId(), order.getAppOrderId());
        LOG.info("[MockAdapter.respStr] with respStr:{}", oriOrder);
        return oriOrder;
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        // TODO Auto-generated method stub
        return null;
    }
}
