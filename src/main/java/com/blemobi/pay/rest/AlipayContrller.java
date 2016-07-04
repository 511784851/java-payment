package com.blemobi.pay.rest;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * @author andy.zhao@blemobi.com 
 * 微信支付接口类
 */
@Path("/alipay")
public class AlipayContrller {

	/**
	 * @param amount
	 *            支付金额（单位：分）
	 * @return String 返回预支付签名信息
	 * @throws Exception
	 * @throws ChatException
	 *             抛出ChatException异常
	 */
	@GET
	@Path("paySign")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage paySign(@QueryParam("uuid") String uuid, @QueryParam("token") String token,
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