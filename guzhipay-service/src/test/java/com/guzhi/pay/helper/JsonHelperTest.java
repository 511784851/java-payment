/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;

/**
 * 测试JSON与对象间的转换效果
 * 
 * @author administrator
 */
public class JsonHelperTest {

    private Logger log = LoggerFactory.getLogger(JsonHelperTest.class);

    private static final String APP_ID = "APP_ID";
    private static final String APP_ORDER_ID = "APP_ID";
    private static final String NOTIFY_URL = "NOTIFY_URL";
    private static final String FORMATE = "yyyyMMddHHmmss";

    @Test
    public void testPayOrderToJsonSuccess() {
        String dateStr = new SimpleDateFormat(FORMATE).format(new Date());

        PayOrder payOrder = new PayOrder();
        payOrder.setLastUpdateTime(dateStr);
        payOrder.setAppId(APP_ID);
        payOrder.setAppOrderId(APP_ORDER_ID);
        payOrder.setNotifyUrl(NOTIFY_URL);
        String json = JsonHelper.payOrderToRespJson(payOrder);

        // fields with @JsonView({ RespView.class })
        Assert.assertTrue(json.contains(APP_ID));
        Assert.assertTrue(json.contains("submitTime")); // auto generated
        Assert.assertTrue(json.contains("lastUpdateTime"));

        // fields without @JsonView({ RespView.class })
        Assert.assertFalse(json.contains(NOTIFY_URL));

    }

    @Test
    public void testPayOrderToJsonStatusNeverEmpty() {
        String json = JsonHelper.payOrderToRespJson(null);
        Assert.assertFalse(json.contains("StatusCode"));
        Assert.assertFalse(json.contains("StatusMsg"));

        PayOrder payOrder = new PayOrder();
        json = JsonHelper.payOrderToRespJson(payOrder);
        Assert.assertFalse(json.contains("StatusCode"));
        Assert.assertFalse(json.contains("StatusMsg"));
    }

    @Test
    public void testJsonToPayOrderSuccess() {
        String json = "{\"appId\":\"APP_ID\",\"submitTime\":\"20130307181811\",\"chOrderId\":\"CH_ORDER_ID\"}";

        PayOrder payOrder = JsonHelper.reqJsonToPayOrder(json, PayOrder.PayReqView.class);

        // fields with @JsonView({ ReqView.class })
        Assert.assertEquals(payOrder.getAppId(), APP_ID);
        Assert.assertNotNull(payOrder.getSubmitTime()); // auto generated

        // fields without @JsonView({ ReqView.class })
        Assert.assertNull(payOrder.getNotifyUrl());

        // empty json structure
        Assert.assertNotNull(JsonHelper.reqJsonToPayOrder("{}", PayOrder.PayReqView.class));
    }

    @Test
    public void testJsonToPayOrderFail() {
        String jsonCorrect = "{\"appId\":\"APP_ID\"}";
        String jsonIncorrect = "{\"appId\":\"APP_ID\",";

        // wrong view class
        try {
            JsonHelper.reqJsonToPayOrder(jsonCorrect, PayOrder.class);
            Assert.fail("should throw PayException!");
        } catch (PayException e) {
            log.info("Junit test success, get expected exception: {}", ReflectionToStringBuilder.toString(e));
        }

        // empty string
        try {
            JsonHelper.reqJsonToPayOrder("", PayOrder.PayReqView.class);
            Assert.fail("should throw PayException!");
        } catch (PayException e) {
            log.info("Junit test success, get expected exception: {}", ReflectionToStringBuilder.toString(e));
        }

        // json format error
        try {
            JsonHelper.reqJsonToPayOrder(jsonIncorrect, PayOrder.PayReqView.class);
            Assert.fail("should throw PayException!");
        } catch (PayException e) {
            log.info("Junit test success, get expected exception: {}", ReflectionToStringBuilder.toString(e));
        }
    }
}
