package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class RewardProcessTest {

	@Before
	public void setup() {

	}

	@Test
	public void Reward() throws Exception {
		StringBuffer basePath = new StringBuffer("/payment/v1/reward/ordinary");
		POrdinRedEnve ordinaryRed = POrdinRedEnve.newBuilder().setMoney(20000).setContent("恭喜发财，大吉大利")
				.setReceUuid("1468419313301436968").build();
		byte[] body = ordinaryRed.toByteArray();
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, body,
				"application/x-protobuf");
		PMessage message = httpClient.postBodyMethod();
		assertEquals("PRedPay", message.getType());
	}
}
