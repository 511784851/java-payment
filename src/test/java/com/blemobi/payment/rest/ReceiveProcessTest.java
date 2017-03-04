package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class ReceiveProcessTest {

	@Before
	public void setup() {

	}

	@Test
	public void tesReceive() throws Exception {
		StringBuffer basePath = new StringBuffer(
				"/v1/payment/receive/redEnve?ord_no=320170301286602935822061568&uuid=1471175703665920837");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PRedInfo", message.getType());
	}

	// @Test
	public void tesFind() throws Exception {
		StringBuffer basePath = new StringBuffer(
				"/v1/payment/find/receRedEnve?ord_no=120170301286530859538976768&uuid=1468419313301436968&last_id=0&count=10");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PRedInfo", message.getType());
	}
}
