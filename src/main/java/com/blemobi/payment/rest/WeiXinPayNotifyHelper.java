package com.blemobi.payment.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blemobi.payment.sql.SqlHelper;
import com.tenpay.RequestHandler;
import com.tenpay.ResponseHandler;
import com.tenpay.client.ClientResponseHandler;
import com.tenpay.client.TenpayHttpClient;
import com.tenpay.util.ConstantUtil;

import lombok.extern.log4j.Log4j;

/**
 * 微信支付服务端简单示例
 * 
 * @author seven_cm
 * @dateTime 2014-11-29
 */

@Log4j
public class WeiXinPayNotifyHelper {
	public static String payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// ---------------------------------------------------------
		// 财付通支付通知（后台通知）示例，商户按照此文档进行开发即可
		// ---------------------------------------------------------
		// 商户号
		String partner = ConstantUtil.PARTNER;

		// 密钥
		String key = ConstantUtil.PARTNER_KEY;

		// 创建支付应答对象
		ResponseHandler resHandler = new ResponseHandler(request, response);
		resHandler.setKey(key);

		// 判断签名
		if (resHandler.isTenpaySign()) {

			// 通知id
			String notify_id = resHandler.getParameter("notify_id");

			// 创建请求对象
			RequestHandler queryReq = new RequestHandler(null, null);
			// 通信对象
			TenpayHttpClient httpClient = new TenpayHttpClient();
			// 应答对象
			ClientResponseHandler queryRes = new ClientResponseHandler();

			// 通过通知ID查询，确保通知来至财付通
			queryReq.init();
			queryReq.setKey(key);
			queryReq.setGateUrl("https://gw.tenpay.com/gateway/verifynotifyid.xml");
			queryReq.setParameter("partner", partner);
			queryReq.setParameter("notify_id", notify_id);

			// 通信对象
			httpClient.setTimeOut(5);
			// 设置请求内容
			httpClient.setReqContent(queryReq.getRequestURL());
			log.info("queryReq:" + queryReq.getRequestURL());
			// 后台调用
			if (httpClient.call()) {
				// 设置结果参数
				queryRes.setContent(httpClient.getResContent());
				log.info("queryRes:" + httpClient.getResContent());
				queryRes.setKey(key);

				// 获取返回参数
				String retcode = queryRes.getParameter("retcode");
				String trade_state = queryRes.getParameter("trade_state");

				String trade_mode = queryRes.getParameter("trade_mode");

				// 判断签名及结果
				if (queryRes.isTenpaySign() && "0".equals(retcode) && "0".equals(trade_state)
						&& "1".equals(trade_mode)) {
					log.info("订单查询成功");
					// 取结果参数做业务处理
					log.info("out_trade_no:" + queryRes.getParameter("out_trade_no") + " transaction_id:"
							+ queryRes.getParameter("transaction_id"));
					log.info("trade_state:" + queryRes.getParameter("trade_state") + " total_fee:"
							+ queryRes.getParameter("total_fee"));
					// 如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
					log.info("discount:" + queryRes.getParameter("discount") + " time_end:"
							+ queryRes.getParameter("time_end"));
					// ------------------------------
					// 处理业务开始
					// ------------------------------

					// 处理数据库逻辑
					// 注意交易单不要重复处理
					// 注意判断返回金额
					String openid = queryRes.getParameter("openid");// 支付用户唯一标识
					String trade_type = "WX";// 支付方式：微信
					String bank_type = queryRes.getParameter("bank_type");// 付款银行
					String amount = queryRes.getParameter("total_fee");// 支付金额
					String fee_type = queryRes.getParameter("fee_type");// 货币类型
					String transaction_id = queryRes.getParameter("transaction_id");// 交易单号
					String order_no = queryRes.getParameter("order_no");// 订单号
					String pay_statu = queryRes.getParameter("result_code");// 支付结果
					String err_code = queryRes.getParameter("err_code");// 支付错误代码
					String err_code_des = queryRes.getParameter("err_code_des");// 支付错误描述 
					String time_end = queryRes.getParameter("time_end");// 支付完成时间
					
					SqlHelper.savePayResultInfo(openid, trade_type, bank_type, amount, fee_type, transaction_id, order_no, pay_statu, err_code, err_code_des, time_end);
					// ------------------------------
					// 处理业务完毕
					// ------------------------------
					return "Success";
				} else {
					// 错误时，返回结果未签名，记录retcode、retmsg看失败详情。
					log.info("查询验证签名失败或业务错误");
					log.info("retcode:" + queryRes.getParameter("retcode") + " retmsg:" + queryRes.getParameter("retmsg"));
				}

			} else {
				log.info("后台调用通信失败");

				log.info(httpClient.getResponseCode());
				log.info(httpClient.getErrInfo());
				// 有可能因为网络原因，请求已经处理，但未收到应答。
			}
		} else {
			log.info("通知签名验证失败");
		}
		
		return "fall";
	}

}