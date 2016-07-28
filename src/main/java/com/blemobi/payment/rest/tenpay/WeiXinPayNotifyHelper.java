package com.blemobi.payment.rest.tenpay;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blemobi.payment.sql.SqlHelper;
import com.blemobi.payment.util.CommonUtil;
import com.tenpay.util.ConstantUtil;
import com.tenpay.util.GetWxOrderno;
import com.tenpay.util.RequestHandler;

import lombok.extern.log4j.Log4j;

/**
 * 微信支付服务端简单示例
 * 
 * @author seven_cm
 * @dateTime 2014-11-29
 */

@Log4j
public class WeiXinPayNotifyHelper {
	private static GetWxOrderno getWxOrderno = new GetWxOrderno();

	public static String payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String[]> mspMap = request.getParameterMap();
		Set<String> set = mspMap.keySet();
		String msgxml = "";
		for (String k : set) {
			msgxml = k;
		}
		Map map = getWxOrderno.doXMLParse(msgxml);
		log.info("微信支付异步通知内容: " + map);
		String return_code = CommonUtil.getMapValue(map, "return_code");
		if ("SUCCESS".equals(return_code)) {// 通信成功
			// 验证签名
			String sign = CommonUtil.getMapValue(map, "sign");
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			Set<String> sets = map.keySet();
			for (String key : sets) {
				if (!"sign".equals(key)) {
					packageParams.put(key, map.get(key).toString());
				}
			}

			RequestHandler reqHandler = new RequestHandler(request, response);
			reqHandler.init(ConstantUtil.APP_ID, ConstantUtil.APP_SECRET, ConstantUtil.PARTNER_KEY);
			String endsign = reqHandler.createSign(packageParams);
			if (sign.equals(endsign)) {// 签名ok
				int pay_statu = 0; // 支付结果 1-支付成功，2-支付失败
				String result_code = CommonUtil.getMapValue(map, "result_code");
				if ("SUCCESS".equals(result_code)) {// 交易成功
					pay_statu = 1;
				} else if ("FAIL".equals(result_code)) {// 交易失败
					pay_statu = 2;
				}

				if (pay_statu != 0) {
					String out_trade_no = CommonUtil.getMapValue(map, "out_trade_no");
					String attach = CommonUtil.getMapValue(map, "attach");
					String total_fee = CommonUtil.getMapValue(map, "total_fee");
					String fee_type = CommonUtil.getMapValue(map, "fee_type");
					String bank_type = CommonUtil.getMapValue(map, "bank_type");
					String cash_fee = CommonUtil.getMapValue(map, "cash_fee");
					String is_subscribe = CommonUtil.getMapValue(map, "is_subscribe");
					String nonce_str = CommonUtil.getMapValue(map, "nonce_str");
					String openid = CommonUtil.getMapValue(map, "openid");
					String sub_mch_id = CommonUtil.getMapValue(map, "sub_mch_id");
					String time_end = CommonUtil.getMapValue(map, "time_end");
					String trade_type = CommonUtil.getMapValue(map, "trade_type");
					String transaction_id = CommonUtil.getMapValue(map, "transaction_id");
					String err_code = CommonUtil.getMapValue(map, "err_code");
					String err_code_des = CommonUtil.getMapValue(map, "err_code_des");

					long time = CommonUtil.dateTimeStamp(time_end, "yyyyMMddHHmmss");

					SqlHelper.savePayResultInfo(pay_statu, openid, bank_type, Long.parseLong(total_fee), transaction_id,
							out_trade_no, err_code, err_code_des, time);

					return setXml("SUCCESS", "ok");
				}
			} else {
				log.info("签名验证失败:" + endsign);
			}
		}
		return "";
	}

	private static String setXml(String return_code, String return_msg) {
		return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg
				+ "]]></return_msg></xml>";
	}
}