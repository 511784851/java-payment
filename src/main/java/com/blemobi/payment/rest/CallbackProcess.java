package com.blemobi.payment.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.blemobi.payment.model.Transaction;
import com.blemobi.payment.service.CallbackService;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 钱包支付回调接口
 * 
 * @author zhaoyong
 *
 */
@Path("/wallet")
public class CallbackProcess {
	/**
	 * 钱包支付回调
	 * 
	 * @return
	 */
	@GET
	@Path("callback")
	@Produces(MediaTypeExt.APPLICATION_JSON)
	public String callback() {
		String sign = "";
		Transaction transaction = new Transaction();
		CallbackService callbackService = new CallbackService(transaction, sign);
		return callbackService.callback();
	}
}