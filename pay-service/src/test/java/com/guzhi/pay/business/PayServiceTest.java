package com.guzhi.pay.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.guzhi.pay.business.PayService;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.helper.TimeHelper;

@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class PayServiceTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private PayService payService;
    @Autowired
    private DomainResource domainResource;
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
        payOrder.setChId("Zfb");
        payOrder.setPayMethod("Gate");
        payOrder.setAmount(new BigDecimal("0.02"));
        AppChInfo appChInfo = new AppChInfo();
        appChInfo.setChAccountId("2088201564862550");
        payOrder.setAppChInfo(appChInfo);
        payOrder.setAppId(appId);
        payOrder.setSubmitTime(submitTime);
        payOrder.setLastUpdateTime(lastUpdateTime);
        payOrder.setAppOrderId(appOrderId);
        System.out.println(payService.pay(payOrder));
    }

    @AfterTest
    public void realRefund() {
        PayOrder payOrder = domainResource.getPayOrder(appId, appOrderId);
        System.out.println(payOrder);
        List<AppChInfo> appChInfos = domainResource.getAppChInfo(appId, payOrder.getChId(), payOrder.getPayMethod());
        payOrder.setAppChInfo(appChInfos.get(0));
        payOrder.setAppRefundTime(TimeHelper.get(8, new Date()));
        payOrder.setRefundAmount("0.01");
        payService.realRefund(payOrder);
    }

}
