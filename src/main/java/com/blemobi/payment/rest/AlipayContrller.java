package com.blemobi.payment.rest;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.simple.JSONObject;

import com.blemobi.payment.rest.alipay.AliPayUtil;
import com.blemobi.payment.sql.SqlHelper;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * 
 * @author 李子才<davis.lee@blemobi.com>
 * 这是支付宝的支付接口。
 */

@Path("/alipay")
public class AlipayContrller {

	/**
	 * @cookie uuid 用户的uuid
	 * @cookie token 用户的token
	 * @param orderSubject 订单的主题
	 * @param orderBody 订单的内容描述
	 * @param orderPrice 订单的总价（单位：分）。以人民币的元为单位，精细到小数点后面两位。
	 * @return PAlipayOrderInfo 返回订单号和签名信息
	 * @throws Exception 抛出Exception异常
	 */
	@GET
	@Path("paySign")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage paySign(@CookieParam("uuid") String uuid, @CookieParam("token") String token,@QueryParam("uuid") String uuid2, @QueryParam("token") String token2,
			@QueryParam("orderSubject") String orderSubject,@QueryParam("orderBody") String orderBody,@QueryParam("amount") String amount) throws Exception {
		//在此添加参数校验的代码
		
		if(uuid==null || uuid.length()==0)uuid = uuid2;
		
		if(token==null || token.length()==0)token = token2;
		
		return AliPayUtil.paySign(uuid,token,orderSubject,orderBody,amount);
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
	@POST
	@Path("payNotify")
	@Produces("text/plain")
	public String payNotify(@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws Exception {
		//在此添加参数校验的代码
		return AliPayUtil.payNotify(request, response);
	}

}