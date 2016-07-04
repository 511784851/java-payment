package com.blemobi.pay.channel.weixin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blemobi.demo.probuf.PaymentProtos.PWeixin;
import com.blemobi.demo.probuf.ResultProtos.PMessage;
import com.blemobi.pay.util.ReslutUtil;
import com.tenpay.AccessTokenRequestHandler;
import com.tenpay.ClientRequestHandler;
import com.tenpay.PackageRequestHandler;
import com.tenpay.PrepayIdRequestHandler;
import com.tenpay.util.ConstantUtil;
import com.tenpay.util.WXUtil;

import lombok.extern.log4j.Log4j;

/**
 * 微信支付服务端简单示例
 * 
 * @author seven_cm
 * @dateTime 2014-11-29
 */

@Log4j
public class WeiXinPaySignHelper {
	private static final String notify_url = "http://47.88.5.139:8088/WeiXinpay-0.0.1-SNAPSHOT//payNotifyUrl.jsp";// 通知的URL
	private static final String input_charset = "GBK"; // 字符编码

	public static PMessage paySign(String out_trade_no, String body, String total_fee, String fee_type,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		PackageRequestHandler packageReqHandler = new PackageRequestHandler(request, response);// 生成package的请求类
		PrepayIdRequestHandler prepayReqHandler = new PrepayIdRequestHandler(request, response);// 获取prepayid的请求类
		ClientRequestHandler clientHandler = new ClientRequestHandler(request, response);// 返回客户端支付参数的请求类
		packageReqHandler.setKey(ConstantUtil.PARTNER_KEY);

		int errorCode;
		String errorMsg = "";
		// 获取token值
		String token = AccessTokenRequestHandler.getAccessToken();

		log.info("get token: " + token);

		if (!"".equals(token)) {
			// 设置package订单参数
			packageReqHandler.setParameter("bank_type", "WX");// 银行渠道
			packageReqHandler.setParameter("body", body); // 商品描述
			packageReqHandler.setParameter("notify_url", notify_url); // 接收财付通通知的URL
			packageReqHandler.setParameter("partner", ConstantUtil.PARTNER); // 商户号
			packageReqHandler.setParameter("out_trade_no", out_trade_no); // 商家订单号
			packageReqHandler.setParameter("total_fee", total_fee);// 支付金额（单位：分）
			packageReqHandler.setParameter("spbill_create_ip", request.getRemoteAddr()); // 订单生成的机器IP，指用户浏览器端IP
			packageReqHandler.setParameter("fee_type", fee_type); // 币种，1人民币 66
			packageReqHandler.setParameter("input_charset", input_charset); // 字符编码

			// 获取package包
			String packageValue = packageReqHandler.getRequestURL();

			log.info("--------------------payment info--------------------");
			log.info("order number: " + out_trade_no);
			log.info("commodity name: " + body);
			log.info("payment amount: " + total_fee);
			log.info("--------------------payment info--------------------");

			log.info("get package: " + packageValue);

			String noncestr = WXUtil.getNonceStr();
			String timestamp = WXUtil.getTimeStamp();
			String traceid = "";
			//// 设置获取prepayid支付参数
			prepayReqHandler.setParameter("appid", ConstantUtil.APP_ID);
			prepayReqHandler.setParameter("appkey", ConstantUtil.APP_KEY);
			prepayReqHandler.setParameter("noncestr", noncestr);
			prepayReqHandler.setParameter("package", packageValue);
			prepayReqHandler.setParameter("timestamp", timestamp);
			prepayReqHandler.setParameter("traceid", traceid);

			// 生成获取预支付签名
			String sign = prepayReqHandler.createSHA1Sign();
			// 增加非参与签名的额外参数
			prepayReqHandler.setParameter("app_signature", sign);
			prepayReqHandler.setParameter("sign_method", ConstantUtil.SIGN_METHOD);
			String gateUrl = ConstantUtil.GATEURL + token;
			prepayReqHandler.setGateUrl(gateUrl);

			// 获取prepayId
			String prepayid = prepayReqHandler.sendPrepay();

			log.info("get prepayid: " + prepayid);

			// 吐回给客户端的参数
			if (null != prepayid && !"".equals(prepayid)) {
				// 输出参数列表
				clientHandler.setParameter("appid", ConstantUtil.APP_ID);
				clientHandler.setParameter("appkey", ConstantUtil.APP_KEY);
				clientHandler.setParameter("noncestr", noncestr);
				// clientHandler.setParameter("package", "Sign=" +
				// packageValue);
				clientHandler.setParameter("package", "Sign=WXPay");
				clientHandler.setParameter("partnerid", ConstantUtil.PARTNER);
				clientHandler.setParameter("prepayid", prepayid);
				clientHandler.setParameter("timestamp", timestamp);
				// 生成签名
				sign = clientHandler.createSHA1Sign();
				clientHandler.setParameter("sign", sign);

				PWeixin weixin = PWeixin.newBuilder().setAppid(ConstantUtil.APP_ID).setPartnerid(ConstantUtil.PARTNER)
						.setNoncestr(noncestr).setPackage("Sign=WXPay").setTimestamp(timestamp).setPrepayid(prepayid)
						.setSign(sign).build();

				return ReslutUtil.createReslutMessage(weixin);
			} else {
				errorCode = -2;
				errorMsg = "get prepayId error";
			}
		} else {
			errorCode = -1;
			errorMsg = "get Token error";
		}

		return ReslutUtil.createErrorMessage(errorCode, errorMsg);
	}

}