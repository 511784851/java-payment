package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.payment.core.PaymentManager;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class CallbackTest {

    private Cookie[] cookies;

    @Before
    public void setup() {
        cookies = new Cookie[2];
        cookies[0] = new Cookie("uuid", "1468419313301436967");
        cookies[1] = new Cookie("token", "98e7eee14df39598c458fbbfa04843cb");

        String[] arg = new String[] {"-env", "local" };
        try {
            PaymentManager.main(arg);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws Exception {
        StringBuffer path = new StringBuffer(
                "/v1/payment/callback/notify?respstat=0000&respmsg=aa&orderAmount=100&orderNo=1234&orderStatus=S&orderTime=1234567&custOrderNo=21&receiveUid=uuid&sign=cb44b7487735ed2cba4d929d13c53e63");
        BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, null,
                "application/x-www-form-urlencoded");
        PMessage message = httpClient.getMethod();

        assertEquals("success", message.getType());
    }

}
