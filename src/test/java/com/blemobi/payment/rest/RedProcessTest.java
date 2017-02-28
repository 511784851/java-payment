package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRedEnve;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class RedProcessTest {

	private StringBuffer basePath;

	@Before
	public void setup() {

	}

	// @Test
	public void testordinary() throws Exception {
		basePath = new StringBuffer("/payment/v1/send/ordinary");
		POrdinRedEnve ordinaryRed = POrdinRedEnve.newBuilder().setMoney(20000).setContent("恭喜发财，大吉大利")
				.setReceUuid("1468419313301436968").build();
		byte[] body = ordinaryRed.toByteArray();
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, body,
				"application/x-protobuf");
		PMessage message = httpClient.postBodyMethod();
		assertEquals("PRedPay", message.getType());
	}

	// @Test
	public void testGroup() throws Exception {
		basePath = new StringBuffer("/payment/v1/send/group");
		PGroupRedEnve oneRed = PGroupRedEnve.newBuilder().setIsRandom(true).setMoney(80000).setNumber(5)
				.setContent("恭喜发财，大吉大利").addReceUuid("1468419313301436968").addReceUuid("1468419313301436969")
				.addReceUuid("1468419313301436910").build();
		byte[] body = oneRed.toByteArray();
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, body,
				"application/x-protobuf");
		PMessage message = httpClient.postBodyMethod();
		assertEquals("PRedPay", message.getType());
	}

	// @Test
	public void tesReceive() throws Exception {
		basePath = new StringBuffer("/payment/v1/send/receive?ord_no=320170224284697062589730816");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PRedInfo", message.getType());
	}

	@Test
	public void tesHistory() throws Exception {
		basePath = new StringBuffer("/payment/v1/send/history?id=-1&size=10");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PRedInfo", message.getType());
	}
}
