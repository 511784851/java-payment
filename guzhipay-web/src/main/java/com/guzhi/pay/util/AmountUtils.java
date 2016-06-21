/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.util;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

/**
 * 金额相关工具。
 * 
 * @author administrator
 * 
 */
public class AmountUtils {
    /**
     * 校验支付金额和Y币数量，校验通过返回true，否则返回false。<br>
     * rate值是支付平台与业务约定的，针对不同渠道配置的小数。<br>
     * rate=Y币数量/支付金额，原则上不允许rate值大于1。<br>
     * 支付金额为空是通过校验的。
     * 在支付金额不为空的情况下，满足下列规则之一即通过校验：<br>
     * <ol>
     * <li>Y币数量为空</li>
     * <li>rate为空，且支付金额 >= Y币数量</li>
     * <li>rate不为空，且支付金额 x 比率 >= Y币数量</li>
     * </ol>
     * 
     * @return
     */
    public static boolean checkAmountRate(BigDecimal amount, BigDecimal yyAmount, String rate) {
        if (amount == null) {
            return true;
        }
        if (yyAmount == null) {
            return true;
        }
        if (StringUtils.isNotBlank(rate)) {
            if (amount.doubleValue() * Double.valueOf(rate) >= yyAmount.doubleValue()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (amount.doubleValue() >= yyAmount.doubleValue()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
