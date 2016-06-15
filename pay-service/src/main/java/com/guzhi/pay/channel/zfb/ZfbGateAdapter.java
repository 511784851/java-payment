/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.zfb;

import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.domain.PayOrder;

/**
 * 支付宝网关支付方式
 * TODO: 出现过用户用支付宝余额支付失败后又支付成功（并通知了）的情况，如何处理？
 * 
 * @author administrator
 */
@Service("zfbGateAdapter")
public class ZfbGateAdapter extends AbstractZfbAdapter implements ChannelIF {

    @Override
    public String status() {
        return super.status();
    }

    @Override
    public PayOrder pay(PayOrder payOrder) {
        return super.pay(payOrder);
    }

    @Override
    public PayOrder query(PayOrder payOrder) {
        return super.query(payOrder);
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new RuntimeException("cant not refund");
    }
}
