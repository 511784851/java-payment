/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import com.guzhi.pay.channel.yeepay.YeePayHelper;
import com.guzhi.pay.domain.PayOrder;

/**
 * 金额相关测试。
 * 
 * @author administrator
 * 
 */
public class AmountTest {

    public static void main(String args[]) {
        PayOrder payOrder = new PayOrder();
        payOrder.setCardTotalAmount("50.~40.0~30.0");
        String cardTotalAmount = YeePayHelper.getCardTotalAmount(payOrder);
        System.out.println("src:" + payOrder.getCardTotalAmount());
        System.out.println("des:" + cardTotalAmount);
    }
}
