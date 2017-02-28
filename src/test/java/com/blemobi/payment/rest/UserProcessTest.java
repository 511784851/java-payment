package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class UserProcessTest {

	@Before
	public void setup() {

	}

	@Test
	public void test() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/user/thirdToken");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PStringSingle", message.getType());
	}
}
