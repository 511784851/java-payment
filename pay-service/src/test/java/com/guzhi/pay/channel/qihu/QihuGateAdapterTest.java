/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.qihu;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.guzhi.pay.channel.qihu.QihuGateAdapter;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.TimeHelper;

/**
 * @author Administrator
 * 
 */
@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class QihuGateAdapterTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private QihuGateAdapter qihuGateAdapter;

    private static String orderId = "";
    static {
        orderId = String.valueOf(System.currentTimeMillis());
    }

    @Test
    public void testPay() {
        PayOrder payOrder = new PayOrder();
        AppChInfo appChInfo = new AppChInfo();
        appChInfo.setChAccountId("20111117360");
        appChInfo.setChPayKeyMd5("Xy+svEIVDRFiawlv1QwRSQ==");
        appChInfo.setAdditionalInfo("test");
        payOrder.setAppChInfo(appChInfo);
        payOrder.setAppOrderId(orderId);
        payOrder.setBankId("CMB");
        payOrder.setAppId("101");
        payOrder.setProdName("gb充值测试");
        payOrder.setAppOrderTime(TimeHelper.get(8, new Date()));
        payOrder.setAmount(new BigDecimal(0.01));
        payOrder.setUserIp("127.0.0.1");
        payOrder.setReturnUrl("http://www.baidu.com");
        System.out.println(qihuGateAdapter.pay(payOrder).getPayUrl());
        System.out.println(qihuGateAdapter.pay(payOrder));
    }

    @Test
    public void testQuery() {
        PayOrder payOrder = new PayOrder();
        AppChInfo appChInfo = new AppChInfo();
        appChInfo.setChAccountId("20111117360");
        appChInfo.setChPayKeyMd5("Xy+svEIVDRFiawlv1QwRSQ==");
        appChInfo.setAdditionalInfo("test");
        payOrder.setAppChInfo(appChInfo);
        payOrder.setAppOrderId(orderId);
        payOrder.setAmount(new BigDecimal(0.01));
        System.out.println(qihuGateAdapter.query(payOrder));
    }

}
