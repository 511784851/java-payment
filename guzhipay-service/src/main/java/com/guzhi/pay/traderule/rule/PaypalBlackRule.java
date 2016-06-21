/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule.rule;

import com.guzhi.pay.traderule.TradeRuleConsts;

/**
 * @author Administrator
 *  paypal 黑名单
 */
public class PaypalBlackRule extends AbstractBlackRule {


    @Override
    public String getAccountParam() {
        return TradeRuleConsts.PAYERID;
    }

}