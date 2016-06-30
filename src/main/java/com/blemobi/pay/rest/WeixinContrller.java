package com.blemobi.pay.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.simple.JSONObject;

import com.blemobi.pay.channel.weixin.WeiXinPayNotifyHelper;
import com.blemobi.pay.channel.weixin.WeiXinPaySignHelper;
import com.blemobi.pay.sql.SqlHelper;
import com.pakulov.jersey.protobuf.internal.MediaTypeExt;

/**
 * @author andy.zhao@blemobi.com 
 * 微信支付接口类
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
	 * @throws ChatException
	 *             抛出ChatException异常
	 */
	@GET
	@Path("paySign")
	@Produces("text/plain")
	public String paySign(@Context HttpServletRequest request, @Context HttpServletResponse response,
			@QueryParam("uuid") String uuid, @QueryParam("order_no") String order_no, @QueryParam("name") String name,
			@QueryParam("amount") String amount) throws Exception {

		String bank_type = "WX";
		String fee_type = "1";
		String app_ip = request.getRemoteAddr();

		// 保存预支付信息
		SqlHelper.savePayInfo(uuid, bank_type, name, order_no, amount, app_ip, fee_type);

		// 生成预支付签名信息
		JSONObject json = WeiXinPaySignHelper.paySign(order_no, name, amount, fee_type, request, response);

		return json.toJSONString();
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