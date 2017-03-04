package com.blemobi.payment.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.blemobi.library.client.BaseHttpClient;
import com.blemobi.library.client.LocalHttpClient;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

public class BillProcessTest {

	@Before
	public void setup() {

	}

	/**
	 * 查询账单
	 * 
	 * @throws Exception
	 */
	@Test
	public void list() throws Exception {
		StringBuffer basePath = new StringBuffer("/v1/payment/bill/info-list?uuid=1471175703665920836&count=10");
		BaseHttpClient httpClient = new LocalHttpClient("127.0.0.1", 9014, basePath, null, null, null);
		PMessage message = httpClient.getMethod();
		assertEquals("PRedInfo", message.getType());
	}

}
