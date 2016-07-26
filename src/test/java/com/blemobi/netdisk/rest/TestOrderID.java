package com.blemobi.netdisk.rest;

import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;
import com.blemobi.payment.rest.alipay.AliPayUtil;
import com.blemobi.payment.rest.util.IDMake;
import com.blemobi.sep.probuf.PaymentProtos.PAlipayOrderInfo;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PResult;

import lombok.extern.log4j.Log4j;
@Log4j

public class TestOrderID {

	@Test
	public void testOrderID() throws Exception {
		String uuid = "0efe519d-cddf-412c-a5e0-2e8f14f80edb";
		long time = System.currentTimeMillis();
		String amount = "3";
		String orderNo = IDMake.build(uuid, time, Long.parseLong(amount));
		log.info(orderNo);
		
	}

}
