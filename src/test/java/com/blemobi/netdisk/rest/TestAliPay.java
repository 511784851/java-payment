package com.blemobi.netdisk.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;
import com.blemobi.payment.rest.alipay.AliPayUtil;
import com.blemobi.sep.probuf.PaymentProtos.PAlipayOrderInfo;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.blemobi.sep.probuf.ResultProtos.PResult;

import lombok.extern.log4j.Log4j;
@Log4j

public class TestAliPay {
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
	public void testAliPay() throws Exception {
		// TODO Auto-generated method stub
		String uuid = "0efe519d-cddf-412c-a5e0-2e8f14f80edb";
		String token = "EiBmN2UzMzM5ZWFiOGZmZTJkZTg5MTE2NGQ2YjJiOGRiMBjYtte8BQ==";
		String orderSubject = "Subject1";
		String orderBody = "Body1";
		String amount = "1";
		PMessage message = AliPayUtil.paySign(uuid, token, orderSubject, orderBody, amount);
		
		
		String type = message.getType();
		
		log.info("type=["+type+"]");
		if (!"PAlipayOrderInfo".equals(type)) {
			PResult pr = PResult.parseFrom(message.getData());
			log.info("getErrorCode=["+pr.getErrorCode()+"]");
			log.info("getErrorMsg=["+pr.getErrorMsg()+"]");
			throw new Exception("Connet OSS STS Service, response Error info, Response(getErrorCode=["+pr.getErrorCode()+"],getErrorMsg=["+pr.getErrorMsg()+"]");
		}else{
			PAlipayOrderInfo  orderInfo = PAlipayOrderInfo.parseFrom(message.getData().toByteArray());
			log.info("getOrderNo=["+orderInfo.getOrderNo()+"]");
			log.info("getPayInfo=["+orderInfo.getPayInfo()+"]");
		}
	}
	
	@After
	public void tearDown() {
		System.exit(0);
	}
}
