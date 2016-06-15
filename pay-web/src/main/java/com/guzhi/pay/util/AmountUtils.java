/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.util;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

/**
 * 金额相关工具。
 * 
 * @author 
 * 
 */
public class AmountUtils {
    /**
     * 校验支付金额和G币数量，校验通过返回true，否则返回false。<br>
     * rate值是支付平台与业务约定的，针对不同渠道配置的小数。<br>
     * rate=G币数量/支付金额，原则上不允许rate值大于1。<br>
     * 支付金额为空是通过校验的。
     * 在支付金额不为空的情况下，满足下列规则之一即通过校验：<br>
     * <ol>
     * <li>G币数量为空</li>
     * <li>rate为空，且支付金额 >= G币数量</li>
     * <li>rate不为空，且支付金额 x 比率 >= G币数量</li>
     * </ol>
     * 
     * @return
     */
    public static boolean checkAmountRate(BigDecimal amount, BigDecimal gbAmount, String rate) {
        if (amount == null) {
            return true;
        }
        if (gbAmount == null) {
            return true;
        }
        if (StringUtils.isNotBlank(rate)) {
            if (amount.doubleValue() * Double.valueOf(rate) >= gbAmount.doubleValue()) {
                return true;
            } else {
                return false;
            }
        } else {
            if (amount.doubleValue() >= gbAmount.doubleValue()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
