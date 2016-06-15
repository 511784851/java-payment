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

/**
 * 为了配合gate系统，重新命名微信支付.
 * 
 * @author 
 * 
 */
@Service("weixinBalanceAdapter")
public class WeixinBalanceAdapter extends AbstractTenpayAdapter {

    @Override
    public String getBankId(PayOrder payOrder) {
        return "WX";
    }
}