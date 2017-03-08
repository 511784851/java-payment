package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
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
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("POrderPay", message.getType());
	}

	/**
	 * 发群红包
	 * 
	 * @throws Exception
	 */
	// @Test
	public void testGroup() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/redEnve/send-group?uuid=1471175703665920835");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("POrderPay", message.getType());
	}

	/**
	 * 查询红包发送列表
	 * 
	 * @throws Exception
	 */
	@Test
	public void lst() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/redEnve/send-list?uuid=1471175703665920835&count=10");
		BaseHttpClient httpClient = new LocalHttpClient("192.168.7.245", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		System.out.println(message.getType());
		assertEquals("PRedInfo", message.getType());
	}
}
