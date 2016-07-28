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
import com.blemobi.payment.util.ReslutUtil;
import com.blemobi.sep.probuf.PaymentProtos;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.google.common.base.Strings;
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
	 * @param amount 订单的总价（单位：分）。以人民币的元为单位。
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
		
		if(Strings.isNullOrEmpty(uuid)){
			return ReslutUtil.createErrorMessage(2501101, "UUID is empty!");
		}
		
		if(Strings.isNullOrEmpty(token)){
			return ReslutUtil.createErrorMessage(2501102, "Token is empty!");
		}
		
		if(Strings.isNullOrEmpty(orderSubject)){
			return ReslutUtil.createErrorMessage(2501103, "Order Subject is empty!");
		}
		
		if(Strings.isNullOrEmpty(orderBody)){
			return ReslutUtil.createErrorMessage(2501104, "Order Body is empty!");
		}
		if(Strings.isNullOrEmpty(amount)){
			return ReslutUtil.createErrorMessage(2501105, "Amount is empty!");
		}
		
		long fenAmount = 0;
		
		try{
			fenAmount = Long.parseLong(amount);
		}catch(Exception e){
			return ReslutUtil.createErrorMessage(2201106, "Amount isn't Integer!");
		}

		return AliPayUtil.paySign(uuid,token,orderSubject,orderBody,fenAmount);
	}
	
	@GET
	@Path("payStatus")
	@Produces(MediaTypeExt.APPLICATION_PROTOBUF)
	public PMessage paySign(@CookieParam("uuid") String uuid, @CookieParam("token") String token,@QueryParam("orderNo") String orderNo) throws Exception {
		//在此添加参数校验的代码
		String status = "1";
		PaymentProtos.PAlipayOrderStatus aos = PaymentProtos.PAlipayOrderStatus.newBuilder().setOrderNo(uuid).setStatus(status).build();
		PMessage rtn = ReslutUtil.createReslutMessage(aos);
		return	rtn;	
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