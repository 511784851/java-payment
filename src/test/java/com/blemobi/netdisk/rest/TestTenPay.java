package com.blemobi.netdisk.rest;

import javax.servlet.http.Cookie;

import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;
import com.blemobi.payment.util.ClientUtilImpl;
import com.blemobi.payment.util.CommonUtil;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

import lombok.extern.log4j.Log4j;
@Log4j

public class TestTenPay {

	/**
	 * @throws Exception
	 */
	@Test
	public void testUserLevel() throws Exception {
		// TODO Auto-generated method stub
		String[] arg = new String[] { "-env", "local" };
		PaymentManager.main(arg);
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {
		}

		String uuid = "0efe519d-cddf-412c-a5e0-2e8f14f80edb";
		String token = "EiBmN2UzMzM5ZWFiOGZmZTJkZTg5MTE2NGQ2YjJiOGRiMBjYtte8BQ==";
		String orderSubject = "充值";
		String orderBody = "Body1";
		String amount = "50";
		
		ClientUtilImpl clientUtil = new ClientUtilImpl();
		
		Cookie[] cookies = CommonUtil.createLoginCookieParams(uuid, token);
		String url = "http://localhost:9014/payment/weixin/paySign?orderSubject="+orderSubject+"&amount="+amount;
		PMessage message = clientUtil.getMethod(url, null, cookies);
		String type = message.getType();
		
		log.info("type=["+type+"]");
		
//		PWeixinPay weixinPay = PWeixinPay.parseFrom(message.getData());
//		log.info("weixinPay=["+weixinPay+"]");
//		
//		
		System.exit(0);
	}
}
