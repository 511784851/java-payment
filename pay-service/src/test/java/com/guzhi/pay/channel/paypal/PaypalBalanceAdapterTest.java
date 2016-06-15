/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.paypal;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.guzhi.pay.channel.paypal.PaypalBalanceAdapter;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.TimeHelper;

/**
 * @author Administrator
 * 
 */
@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class PaypalBalanceAdapterTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private PaypalBalanceAdapter paypalBalanceAdapter;

    String appOrderId;
    String appId = "101";
    String submitTime = "";
    String lastUpdateTime = "";
    BigDecimal amount = new BigDecimal(10000);
    String payUrl = "http://api.alipay.com?test";

    @BeforeTest
    public void setUp() {
        appOrderId = TimeHelper.get(8, new Date());
        submitTime = TimeHelper.get(8, new Date());
        lastUpdateTime = TimeHelper.get(8, new Date());
    }

    @Test
    public void testPay() {
        PayOrder payOrder = new PayOrder();
        AppChInfo appChInfo = new AppChInfo();
        appChInfo.setChAccountId("seller_api1.gb.com");
        appChInfo.setChPayKeyMd5("MVH43ZJXNFTV9F4A");
        appChInfo.setAdditionalInfo("A6LKvDuBEdgD5Vx6.7t3b55qrTglASbV21zX9U-iNQteUkayEGkrcwAf");
        payOrder.setAppChInfo(appChInfo);
        payOrder.setAppOrderId(appOrderId);
        payOrder.setAppOrderTime(submitTime);
        payOrder.setAmount(new BigDecimal(0.01));
        StringBuilder sb = new StringBuilder();
        PaypalBalanceAdapter.ADDR_PAYPAL_PURVIES = "https://api-3t.sandbox.paypal.com/nvp?";
        sb.append("{").append("\"").append("gbuid").append("\":\"13778000\"").append("}");
        payOrder.setUserId(sb.toString());
        System.out.println(paypalBalanceAdapter.pay(payOrder));
    }

    @AfterTest
    public void testQuery() {
        PayOrder payOrder = new PayOrder();
        AppChInfo appChInfo = new AppChInfo();
        appChInfo.setChAccountId("seller_api1.gb.com");
        appChInfo.setChPayKeyMd5("MVH43ZJXNFTV9F4A");
        appChInfo.setAdditionalInfo("A6LKvDuBEdgD5Vx6.7t3b55qrTglASbV21zX9U-iNQteUkayEGkrcwAf");
        payOrder.setAppChInfo(appChInfo);
        payOrder.setAppOrderId("20130508151830");
        payOrder.setAppOrderTime("20130508151830");
        payOrder.setAmount(new BigDecimal(0.01));
        PaypalBalanceAdapter.ADDR_PAYPAL_PURVIES = "https://api-3t.sandbox.paypal.com/nvp?";
        System.out.println(paypalBalanceAdapter.query(payOrder));
    }

}
