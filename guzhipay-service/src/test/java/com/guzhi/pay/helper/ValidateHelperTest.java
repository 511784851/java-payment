package com.guzhi.pay.helper;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.ValidateHelper;


/**
 * @author administrator
 * @author administrator
 *
 */
public class ValidateHelperTest {
	@Test
	public void validatePayReqQueryReqSuccess() {
		PayOrder payOrder = new PayOrder();
		payOrder.setAppId("appId");
		payOrder.setAppOrderId("appOrderId");
		payOrder.setBankId("bankId");
		payOrder.setPayMethod("payMethod");
		payOrder.setAppOrderTime("appOrderTime");
		// no exception should be thrown
		ValidateHelper.validatePayOrderFields(payOrder, PayOrder.PayReqVal.class);
		ValidateHelper.validatePayOrderFields(payOrder, PayOrder.QueryReqVal.class);
	}
	
	@Test
	public void validatePayReqQueryReqFail(){
		PayOrder payOrder = new PayOrder();
		payOrder.setAppId("appId");
		try{
			ValidateHelper.validatePayOrderFields(payOrder, PayOrder.PayReqVal.class);
			Assert.fail("expect PayException here");
		}catch(Exception e){
		}
		
	}
	
	@Test
	public void validatePayReqFail() {
		PayOrder payOrder = new PayOrder();
		verifyPayException(payOrder, PayOrder.PayReqVal.class, "must not be blank");
	}

	private void verifyPayException(PayOrder payOrder, @SuppressWarnings("rawtypes") Class validateGroup, String expectErrorMsg) {
		try {
			ValidateHelper.validatePayOrderFields(payOrder, validateGroup);
			Assert.fail("expect PayException here!");
		} catch (PayException e) {
			Assert.assertEquals(e.getStatusCode(), Consts.SC.DATA_ERROR);
			Assert.assertTrue(e.getMessage().contains(expectErrorMsg));
		}
	}
}
