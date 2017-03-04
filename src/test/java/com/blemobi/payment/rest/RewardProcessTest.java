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

	/**
	 * 打赏
	 * 
	 * @throws Exception
	 */
	//@Test
	public void reward() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/reward/send?uuid=1468419313301436935");
		POrdinRedEnve ordinaryRed = POrdinRedEnve.newBuilder().setMoney(2000).setContent("恭喜发财，大吉大利")
				.setReceUuid("1468419313301436963").build();
		byte[] body = ordinaryRed.toByteArray();
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, body,
				"application/x-protobuf");
		PMessage message = httpClient.postBodyMethod();
		assertEquals("PRedPay", message.getType());
	}

	/**
	 * 打赏历史
	 * 
	 * @throws Exception
	 */
	// @Test
	public void list() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/reward/list?uuid=1468419313301436937&type=1&count=10");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PRedInfo", message.getType());
	}

	/**
	 * 查看打赏想起过和打赏记录
	 * 
	 * @throws Exception
	 */
	@Test
	public void info() throws Exception {
		StringBuffer basePath = new StringBuffer(
				"/v1/payment/reward/info-list?ord_no=420170301286536886875459584&uuid=1468419313301436968&count=10");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PRewardInfoList", message.getType());
	}
}
