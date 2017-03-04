package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.sep.probuf.PaymentProtos.PGroupRedEnve;
import com.blemobi.sep.probuf.PaymentProtos.POrdinRedEnve;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class SendProcessTest {

	@Before
	public void setup() {

	}

	/**
	 * 发普通红包
	 * 
	 * @throws Exception
	 */
	// @Test
	public void testOrdinary() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/redEnve/send-ordin?uuid=1471175703665920835");
		POrdinRedEnve ordinaryRed = POrdinRedEnve.newBuilder().setMoney(2600).setContent("恭喜发财，大吉大利")
				.setReceUuid("1468419313301436968").build();
		byte[] body = ordinaryRed.toByteArray();
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, body,
				"application/x-protobuf");
		PMessage message = httpClient.postBodyMethod();
		assertEquals("PRedPay", message.getType());
	}

	/**
	 * 发群红包
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGroup() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/redEnve/send-group?uuid=1471175703665920835");
		PGroupRedEnve oneRed = PGroupRedEnve.newBuilder().setIsRandom(false).setMoney(8000).setNumber(2)
				.setContent("恭喜发财，大吉大利").build();
		byte[] body = oneRed.toByteArray();
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, body,
				"application/x-protobuf");
		PMessage message = httpClient.postBodyMethod();
		assertEquals("PRedPay", message.getType());
	}

	/**
	 * 查询红包发送列表
	 * 
	 * @throws Exception
	 */
	@Test
	public void lst() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/redEnve/list?uuid=1471175703665920835&count=10");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PRedInfo", message.getType());
	}
}
