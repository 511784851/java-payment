package com.blemobi.pay.rest;

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

import com.blemobi.demo.probuf.ResultProtos.PMessage;
import com.blemobi.pay.rest.alipay.AliPayUtil;
import com.blemobi.pay.sql.SqlHelper;
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
	public PMessage paySign(@CookieParam("uuid") String uuid, @CookieParam("token") String token,
			@QueryParam("orderSubject") String orderSubject,@QueryParam("orderBody") String orderBody,@QueryParam("orderPrice") String orderPrice) throws Exception {
		//在此添加参数校验的代码
		
		return AliPayUtil.paySign(uuid,token,orderSubject,orderBody,orderPrice);
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