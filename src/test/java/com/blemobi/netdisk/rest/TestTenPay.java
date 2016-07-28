package com.blemobi.netdisk.rest;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;
import com.blemobi.payment.util.ClientUtilImpl;
import com.blemobi.payment.util.CommonUtil;
import com.blemobi.sep.probuf.ResultProtos.PMessage;

import lombok.extern.log4j.Log4j;

@Log4j

public class TestTenPay {
	
	@Test
	public void testPaySign() throws Exception {
		// TODO Auto-generated method stub
		String[] arg = new String[] { "-env", "local" };
		PaymentManager.main(arg);

		String uuid = "0efe519d-cddf-412c-a5e0-2e8f14f80edb";
		String token = "EiBmN2UzMzM5ZWFiOGZmZTJkZTg5MTE2NGQ2YjJiOGRiMBjYtte8BQ==";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("amount", "1"));
		params.add(new BasicNameValuePair("orderSubject", "充值1分"));
		params.add(new BasicNameValuePair("orderBody", "1分"));
		
		Cookie[] cookies = CommonUtil.createLoginCookieParams(uuid, token);
		String url = "http://localhost:9014/payment/weixin/paySign";
		ClientUtilImpl clientUtil = new ClientUtilImpl();
		PMessage message = clientUtil.getMethod(url, params, cookies);

		// String type = message.getType();

		log.info("message=[" + message + "]");
	}
	
	//@Test
	public void testPayNotify() throws Exception {
		// TODO Auto-generated method stub
		String[] arg = new String[] { "-env", "local" };
		PaymentManager.main(arg);

		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<appid><![CDATA[wx2421b1c4370ec43b]]></appid>");
		sb.append("<attach><![CDATA[支付测试]]></attach>");
		sb.append("<bank_type><![CDATA[CFT]]></bank_type>");
		sb.append("<fee_type><![CDATA[CNY]]></fee_type>");
		sb.append("<is_subscribe><![CDATA[Y]]></is_subscribe>");
		sb.append("<mch_id><![CDATA[10000100]]></mch_id>");
		sb.append("<nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>");
		sb.append("<openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid>");
		sb.append("<out_trade_no><![CDATA[13358548966252038801]]></out_trade_no>");
		sb.append("<result_code><![CDATA[SUCCESS]]></result_code>");
		sb.append("<return_code><![CDATA[SUCCESS]]></return_code>");
		sb.append("<sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>");
		sb.append("<sub_mch_id><![CDATA[10000100]]></sub_mch_id>");
		sb.append("<time_end><![CDATA[20140903131540]]></time_end>");
		sb.append("<total_fee>1</total_fee>");
		sb.append("<trade_type><![CDATA[JSAPI]]></trade_type>");
		sb.append("<transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id>");
		sb.append("</xml>");

		String url = "http://localhost:9014/payment/weixin/payNotify";
		String e = postBodyMethod(url, sb.toString().getBytes(), null);

		log.info("type=[" + e + "]");

		System.exit(0);
	}

	
	private static String postBodyMethod(String urlPath, byte[] body, Cookie[] cookies) throws Exception {
		URL url = new URL(urlPath);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setUseCaches(false);

		urlConnection.setRequestMethod("POST");

		if (cookies != null) {
			StringBuilder sb = new StringBuilder();
			for (Cookie ck : cookies) {
				sb.append(ck.getName()).append('=').append(ck.getValue()).append(";");
			}
			urlConnection.setRequestProperty("Cookie", sb.toString());
		}

		OutputStream out = urlConnection.getOutputStream();

		out.write(body);
		out.flush();
		out.close();

		InputStream inputStream = urlConnection.getInputStream();
		String encoding = urlConnection.getContentEncoding();
		String bodyString = IOUtils.toString(inputStream, encoding);

		return bodyString;
	}
}
