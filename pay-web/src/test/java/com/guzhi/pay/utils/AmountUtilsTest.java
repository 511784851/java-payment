/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.utils;

import java.math.BigDecimal;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.guzhi.pay.util.AmountUtils;

/**
 * 金额工具测试类
 * 
 * @author 
 * 
 */
public class AmountUtilsTest {

    @Test
    public void checkAmountRateTest() {
        // 支付金额为空，G币数量为空，返回true
        Assert.assertTrue(AmountUtils.checkAmountRate(null, null, null));
        // 支付金额非空，G币数量为空，返回true
        Assert.assertTrue(AmountUtils.checkAmountRate(new BigDecimal("3.00"), null, null));
        // 支付金额为空，G币数量非空，返回true
        Assert.assertTrue(AmountUtils.checkAmountRate(null, new BigDecimal("3.00"), null));

        // rate为空，且支付金额 < G币数量，返回false
        Assert.assertFalse(AmountUtils.checkAmountRate(new BigDecimal("3.00"), new BigDecimal("4.00"), null));
        // rate为空，且支付金额 > G币数量，返回true
        Assert.assertTrue(AmountUtils.checkAmountRate(new BigDecimal("3.00"), new BigDecimal("2.00"), null));
        // rate为空，且支付金额 = G币数量，返回true
        Assert.assertTrue(AmountUtils.checkAmountRate(new BigDecimal("3.00"), new BigDecimal("3.00"), null));

        // rate不为空，且支付金额 x 比率 < G币数量，返回false
        Assert.assertFalse(AmountUtils.checkAmountRate(new BigDecimal("3.00"), new BigDecimal("2.00"), "0.50"));
        // rate不为空，且支付金额 x 比率 = G币数量，返回true
        Assert.assertTrue(AmountUtils.checkAmountRate(new BigDecimal("3.00"), new BigDecimal("1.50"), "0.50"));
        // rate不为空，且支付金额 x 比率 > G币数量，返回true
        Assert.assertTrue(AmountUtils.checkAmountRate(new BigDecimal("3.00"), new BigDecimal("0.50"), "0.50"));
    }
}