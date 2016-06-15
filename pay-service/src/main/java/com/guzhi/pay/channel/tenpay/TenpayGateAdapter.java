/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.tenpay;

import org.springframework.stereotype.Service;

import com.guzhi.pay.domain.PayOrder;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 财付通网关支付.
 * 
 * @author 
 * 
 */
@Service("tenpayGateAdapter")
public class TenpayGateAdapter extends AbstractTenpayAdapter {

    @Override
    public String status() {
        throw new NotImplementedException();
    }

    @Override
    public PayOrder refund(PayOrder payOrder) {
        throw new NotImplementedException();
    }

    @Override
    public String getBankId(PayOrder payOrder) {
        return payOrder.getBankId();
    }
}