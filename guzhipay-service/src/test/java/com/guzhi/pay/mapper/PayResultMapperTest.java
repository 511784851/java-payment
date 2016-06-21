package com.guzhi.pay.mapper;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeTest;

import com.guzhi.pay.mapper.PayResultMapper;

@ContextConfiguration(locations = "classpath*:dao-context.xml")
public class PayResultMapperTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private PayResultMapper mapper;
    String appId = "30";
    String appOrderId = "";
    BigDecimal amount = new BigDecimal(10000);

    @BeforeTest
    public void setUp() {
        appOrderId = System.currentTimeMillis() + "";
    }

    /*
     * @Test
     * public void createPayResult() {
     * mapper.deletePayResult(appId, appOrderId);
     * PayResult r = new PayResult();
     * r.setAmount(amount);
     * r.setAppId(appId);
     * r.setAppOrderId(appOrderId);
     * r.setBankDealTime("20121212121212");
     * r.setBankId("ABC");
     * r.setBankDealId("bank_deal_id");
     * r.setChFee(new BigDecimal(3));
     * r.setStatusCode("CODE_SUCCESS");
     * r.setStatusMsg("status_msg");
     * int result = mapper.createPayResult(r);
     * Assert.assertEquals(result, 1);
     * }
     */
}
