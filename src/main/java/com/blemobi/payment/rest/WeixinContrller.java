package com.blemobi.payment.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.blemobi.payment.rest.tenpay.WeiXinPayNotifyHelper;
import com.blemobi.payment.rest.tenpay.WeiXinPaySignHelper;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * @author andy.zhao@blemobi.com 微信支付接口类
 */
@Path("/weixin")
public class WeixinContrller {

	/**
	 * 获取微信预支付签名信息
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param order_no
	 *            订单号
	 * @param name
	 *            商品名称
	 * @param amount
	 *            支付金额（单位：分）
	 * @return String 返回预支付签名信息
	 * @throws Exception
	 */
	@GET
	@Path("paySign")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage paySign(@Context HttpServletRequest request, @Context HttpServletResponse response,
			@CookieParam("uuid") String uuid, @QueryParam("orderSubject") String orderSubject, @QueryParam("orderBody") String orderBody,
			@QueryParam("amount") int amount) throws Exception {

		// 生成预支付签名信息 
		
		System.out.println("uuid: " + uuid + ";orderSubject: "+orderSubject + ";amount: "+amount);
		
		PMessage message = WeiXinPaySignHelper.paySign(orderSubject, amount, uuid, request, response);

		return message;
	}

	/**
	 * 微信预支付通知
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 */
	@GET
	@Path("payNotify")
	@Produces(MediaTypeExt.APPLICATION_JSON)
	public String payNotify(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws Exception {
		WeiXinPayNotifyHelper.payNotify(request, response);
		return "Success";
	}

}