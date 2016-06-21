package com.guzhi.pay.helper;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.SecureHelper;
import com.guzhi.pay.helper.TimeHelper;

public class SecureHelperTest {
    private String appId = "30";
    private String appOrderId = "test-apporderid-12345";
    private String payerName = "强人";
    private String key = "sal2#%3A5*@Vg45";
    private String submitTime = TimeHelper.getFormattedTime();
    private BigDecimal amount = new BigDecimal(10000);

    @Test
    public void genMd5SignNormal() {
        String sign = SecureHelper.genMd5Sign(key, createDataStr());
        Assert.assertNotNull(sign, "the md5 sign should not null");
    }

    @Test
    public void genMd5SignFieldMissed() {
        try {
            SecureHelper.genMd5Sign(key, null);
            Assert.fail("should fail when field missed or empty");
        } catch (PayException e) {
            Assert.assertEquals(e.getStatusCode(), Consts.SC.SECURE_ERROR);
            Assert.assertTrue(e.getMessage().contains("field missed or empty"));
        }
    }

    @Test
    public void verifyMd5SignSuccess() throws Exception {
        String sign = createSignWithApacheTool();
        Thread.sleep(1000);
        SecureHelper.verifyMd5Sign(key, sign, createDataStr());
    }

    @Test
    public void verifyMd5SignFail() {
        String sign = createSignWithApacheTool() + "this_cause_verify_fail";
        try {
            SecureHelper.verifyMd5Sign(key, sign, createDataStr());
            Assert.fail("should fail when sign verify failed");
        } catch (PayException e) {
            Assert.assertEquals(e.getStatusCode(), Consts.SC.SECURE_ERROR);
            Assert.assertTrue(e.getMessage().contains("sign not match"));
        }
    }

    /**
     * 在SecureHelper中使用了Spring的工具，这里使用Apache Commons的工具作验证
     */
    private String createSignWithApacheTool() {
        String strForGen = "data=" + createDataStr() + "&key=" + key;
        String sign = DigestUtils.md5Hex(strForGen.getBytes(Charset.forName("UTF-8")));
        return sign;
    }

    private String createDataStr() {
        PayOrder payOrder = new PayOrder();
        payOrder.setAppId(appId);
        payOrder.setSubmitTime(submitTime); // 如果时间截不一样，可能会导致生成的MD5始终不一样
        payOrder.setAppOrderId(appOrderId);
        payOrder.setUserName(payerName);
        payOrder.setAmount(amount);
        payOrder.setUserId(" "); // blank string
        payOrder.setUserContact(" "); // empty string
        payOrder.setLastUpdateTime(submitTime);
        return JsonHelper.payOrderToRespJson(payOrder);
    }
}
