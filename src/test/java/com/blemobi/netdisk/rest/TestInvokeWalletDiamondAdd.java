package com.blemobi.netdisk.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;
import com.blemobi.payment.rest.common.WalletTools;
import com.blemobi.payment.util.ClientUtilImpl;
import com.blemobi.payment.util.CommonUtil;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

import lombok.extern.log4j.Log4j;

@Log4j

public class TestInvokeWalletDiamondAdd {
	@Before
	public void setUp() throws Exception {
		String[] arg = new String[] { "-env", "local" };
		PaymentManager.main(arg);
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
		}
	}

	@Test
	public void testInvokeWalletDiamondAdd() throws Exception {
		// TODO Auto-generated method stub
		String uuid = "0efe519d-cddf-412c-a5e0-2e8f14f80edb";
		String token = "EiBmN2UzMzM5ZWFiOGZmZTJkZTg5MTE2NGQ2YjJiOGRiMBjYtte8BQ==";
		String orderNo = "13358548966252038801";
		long amount = 300L;
		WalletTools.invokeWalletDiamondAdd(uuid, token, amount, orderNo);
	}


	@After
	public void tearDown() {
		System.exit(0);
	}
}
