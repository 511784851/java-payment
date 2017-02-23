package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.payment.core.PaymentManager;
import com.blemobi.sep.probuf.PaymentProtos.POneRed;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class RedProcessTest {

	private StringBuffer basePath;
	private Cookie[] cookies;

	@Before
	public void setup() {
		basePath = new StringBuffer("/payment/v1/send/ordinary");
		cookies = new Cookie[2];
		cookies[0] = new Cookie("uuid", "1468419313301436967");
		cookies[1] = new Cookie("token", "98e7eee14df39598c458fbbfa04843cb");

		String[] arg = new String[] { "-env", "local" };
		try {
			PaymentManager.main(arg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test() throws Exception {
		POneRed oneRed = POneRed.newBuilder().setAmount(100).setContent("恭喜发财，大吉大利")
				.setRecuuid("1468419313301436968").build();
		byte[] body = oneRed.toByteArray();
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, body, "application/x-protobuf");
		PMessage message = httpClient.postBodyMethod();
		assertEquals("PRedPay", message.getType());
	}

}
