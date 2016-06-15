/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.traderule;

import java.util.HashMap;
import java.util.Map;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.guzhi.pay.traderule.TradeRuleUtils;

/**
 * @author Administrator
 *
 */
@ContextConfiguration(locations = {"classpath*:dao-context.xml","classpath*:traderule-context.xml"})
public class TradeRuleUtilsTest extends AbstractTestNGSpringContextTests{
    @Test
    public void testPayForPaypal(){
        String scene = "payForPaypal";
        Map<String,String> params = new HashMap<String,String>();
        params.put("gbuid", "1234");
        params.put("chId", "Paypal");
        params.put("amount", "100");
        TradeRuleUtils.dispatch(scene, params);
    }
    @Test
    public void testPaypalDetail(){
        String scene = "paypalDetail";
        Map<String,String> params = new HashMap<String,String>();
        params.put("gbuid", "1234");
        params.put("chId", "Paypal");
        params.put("amount", "100");
        params.put("payerstatus", "verified");
        params.put("payerId", "ceshi");
        params.put("ip", "127.0.0.1");
        params.put("appId", "101");
        params.put("appOrderId", "105");
        TradeRuleUtils.dispatch(scene, params);
    }
}
