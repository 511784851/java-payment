/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.jw;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.guzhi.pay.channel.jw.JwJkAdapter;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.DESEncrypt;
import com.guzhi.pay.helper.TimeHelper;

/**
 * @author Administrator
 * 
 */
@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class JwAdapterTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private JwJkAdapter jwJkAdapter;

    String appOrderId;
    String appId = "101";
    String submitTime = "";
    String lastUpdateTime = "";
    String passkey = "test_key";
    String cardPass = "test_pass";

    @BeforeTest
    public void setUp() {
        appOrderId = TimeHelper.get(8, new Date());
        submitTime = TimeHelper.get(8, new Date());
        lastUpdateTime = TimeHelper.get(8, new Date());

    }

    @Test
    public void testPay() {
        try {
            PayOrder payOrder = new PayOrder();
            AppChInfo appChInfo = new AppChInfo();
            appChInfo.setChAccountId("1503152");
            appChInfo.setChAccountsKeyMd5("B3hCE1YFdyob");
            payOrder.setAppChInfo(appChInfo);
            payOrder.setAppOrderId(appOrderId);
            payOrder.setAppOrderTime(submitTime);
            payOrder.setCardNum("1109095023529127");
            payOrder.setCardPass(DESEncrypt.encryptByAES(passkey, cardPass));
            payOrder.setAmount(new BigDecimal(100));

            AppInfo appInfo = new AppInfo();
            appInfo.setAppId("101");
            appInfo.setPasswdKey(passkey);
            payOrder.setAppInfo(appInfo);
            jwJkAdapter.pay(payOrder);
        } catch (PayException e) {
            Assert.assertEquals(Consts.SC.CHANNEL_ERROR, e.getStatusCode());
        }

    }

    @AfterTest
    public void testQuery() {
        try {
            PayOrder payOrder = new PayOrder();
            AppChInfo appChInfo = new AppChInfo();
            appChInfo.setChAccountId("1503152");
            appChInfo.setChAccountsKeyMd5("B3hCE1YFdyob");
            payOrder.setAppChInfo(appChInfo);
            payOrder.setAppOrderId(appOrderId);
            jwJkAdapter.query(payOrder);
        } catch (PayException e) {
            Assert.assertEquals(Consts.SC.CHANNEL_ERROR, e.getStatusCode());
        }
    }
}
