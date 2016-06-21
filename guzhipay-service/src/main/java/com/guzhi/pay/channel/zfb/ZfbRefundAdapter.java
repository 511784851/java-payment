/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.zfb;

import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.domain.PayOrder;

/**
 * 退款处理
 * @author Administrator
 *
 */
@Service("zfbRefundAdapter")
public class ZfbRefundAdapter extends AbstractZfbAdapter  implements ChannelIF{
    @Override
    public String status() {
        throw new RuntimeException("cant not status");
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        throw new RuntimeException("cant not pay");
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        return super.query(payOrder);
    }
    @Override
    public  PayOrder refund(PayOrder payOrder){
        return super.refund(payOrder);
    }
}
