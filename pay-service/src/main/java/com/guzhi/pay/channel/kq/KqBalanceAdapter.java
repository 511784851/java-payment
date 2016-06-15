/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.kq;

import org.springframework.stereotype.Service;

import com.guzhi.pay.channel.kq.gate.KqGateAdapter;
import com.guzhi.pay.domain.PayOrder;

/**
 * @author administrator
 * 
 */
@Service("kqBalanceAdapter")
public class KqBalanceAdapter extends KqGateAdapter {
    public PayOrder pay(PayOrder order) {
        return super.pay(order);
    }
}
