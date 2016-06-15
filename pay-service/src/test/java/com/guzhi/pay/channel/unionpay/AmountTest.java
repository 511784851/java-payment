/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.unionpay;

import java.math.BigDecimal;

/**
 * @author 
 * 
 */
public class AmountTest {

    public static void main(String[] args) {
        BigDecimal amount = new BigDecimal("0.02");
        System.out.println("bigdecimal:0.02\nstring:" + generateCentAmountString(amount));
    }

    public static String generateCentAmountString(BigDecimal amount) {
        String plainAmount = amount.toPlainString();
        Float floatCentAmount = Float.valueOf(plainAmount) * 100;
        return String.valueOf(floatCentAmount.intValue());
    }
}
