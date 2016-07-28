package com.blemobi.payment.rest.tenpay;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blemobi.payment.rest.util.IDMake;
import com.blemobi.payment.sql.SqlHelper;
import com.blemobi.payment.util.ReslutUtil;
import com.blemobi.sep.probuf.PaymentProtos.PWeixinPay;
import com.blemobi.sep.probuf.ResultProtos.PMessage;
import com.tenpay.PrepayIdRequestHandler;
import com.tenpay.util.ConstantUtil;
import com.tenpay.util.RequestHandler;
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
	private static final String fee_type = "1"; // 币种，1人民币 66
	private static final String bank_type = "WX";

	public static PMessage paySign(String orderSubject, String orderBody, long amount, String uuid,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		int errorCode = 190000;
		String errorMsg = "";

		// 金额转化为分为单位
		String out_trade_no = IDMake.build(uuid, System.currentTimeMillis(), amount);
		String spbill_create_ip = request.getRemoteAddr();

		// 保存预支付信息
		SqlHelper.savePayInfo(uuid, bank_type, orderSubject, orderBody, out_trade_no, amount, spbill_create_ip,
				fee_type);

		String noncestr = WXUtil.getNonceStr();
		String timestamp = WXUtil.getTimeStamp();
		String traceid = "";// 附加信息

		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", ConstantUtil.APP_ID);
		packageParams.put("attach", traceid);
		packageParams.put("body", orderSubject);
		packageParams.put("mch_id", ConstantUtil.PARTNER);
		packageParams.put("nonce_str", noncestr);
		packageParams.put("notify_url", ConstantUtil.notify_url);
		packageParams.put("out_trade_no", out_trade_no);
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("total_fee", amount + "");
		packageParams.put("trade_type", "APP");
		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init(ConstantUtil.APP_ID, ConstantUtil.APP_SECRET, ConstantUtil.PARTNER_KEY);
		String sign = reqHandler.createSign(packageParams);// 生成获取预支付签名
		log.info("get prepayid sign: " + sign);
		PrepayIdRequestHandler prepayReqHandler = new PrepayIdRequestHandler(request, response);// 获取prepayid的请求类

		Set<String> set = packageParams.keySet();
		for (String key : set) {
			prepayReqHandler.setParameter(key, packageParams.get(key));
		}

		// 获取prepayId
		prepayReqHandler.setParameter("sign", sign);
		prepayReqHandler.setGateUrl(ConstantUtil.GATEURL);
		String prepayid = prepayReqHandler.sendPrepay();

		log.info("get prepayid: " + prepayid);

		// 吐回给客户端的参数
		if (null != prepayid && !"".equals(prepayid)) {
			SortedMap<String, String> finalpackage = new TreeMap<String, String>();  
			String app_noncestr = WXUtil.getNonceStr();
			String app_timestamp = WXUtil.getTimeStamp();
            finalpackage.put("appid", ConstantUtil.APP_ID);    
            finalpackage.put("timestamp", app_timestamp);    
            finalpackage.put("noncestr", app_noncestr);    
            finalpackage.put("partnerid", ConstantUtil.PARTNER);   
            finalpackage.put("package", "Sign=WXPay");                
            finalpackage.put("prepayid", prepayid);   
            
            String app_sign = reqHandler.createSign(finalpackage);  
            log.info("get app sign: " + app_sign);
            PWeixinPay weixin = PWeixinPay.newBuilder()
					.setAppid(ConstantUtil.APP_ID)
					.setPartnerid(ConstantUtil.PARTNER)
					.setNoncestr(app_noncestr)
					.setPackage("Sign=WXPay")
					.setTimestamp(app_timestamp)
					.setPrepayid(prepayid)
					.setSign(app_sign)
					.setOrderNo(out_trade_no)
					.build();

			return ReslutUtil.createReslutMessage(weixin);
		} else {
			errorCode = -2;
			errorMsg = "get prepayId error";
		}

		return ReslutUtil.createErrorMessage(errorCode, errorMsg);
	}
}