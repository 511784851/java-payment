package com.blemobi.payment.rest;

import java.io.IOException;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;

import lombok.extern.java.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        // StringBuffer path = new StringBuffer(
        // "/v1/payment/callback/notify?respstat=0000&respmsg=aa&orderAmount=100&orderNo=1234&orderStatus=S&orderTime=1234567&custOrderNo=21&receiveUid=uuid&sign=cb44b7487735ed2cba4d929d13c53e63");
        // BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, path, null, null,
        // "application/x-www-form-urlencoded");
        // PMessage message = httpClient.getMethod();
        //
        // assertEquals("success", message.getType());

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("respstat", "0000").add("respmsg", "成功")
                .add("orderAmount", "100").add("orderNo", "1234").add("orderStatus", "1").add("orderTime", "1234567")
                .add("custOrderNo", "520170228286148971922067456").add("receiveUid", "uuid").add("sign", "8675571791398e3cb61e4a35ebc57a97")
                .build();
        Request request = new Request.Builder().url("http://127.0.0.1:9014/v1/payment/callback/notify").post(formBody).build();
        Response resp = client.newCall(request).execute();
        if(resp.isSuccessful()){
            System.out.println(resp.body().string());
        }else{
            System.out.println("failed");
        }
        
    }

}
