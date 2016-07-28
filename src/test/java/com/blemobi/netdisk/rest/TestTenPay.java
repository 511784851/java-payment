package com.blemobi.netdisk.rest;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.Cookie;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.blemobi.payment.core.PaymentManager;
import com.blemobi.payment.util.ClientUtilImpl;
import com.blemobi.payment.util.CommonUtil;

import lombok.extern.log4j.Log4j;
@Log4j

public class TestTenPay {

	/**
	 * @throws Exception
	 */
	@Test
	public void testUserLevel() throws Exception {
		// TODO Auto-generated method stub
		String[] arg = new String[] { "-env", "local" };
		PaymentManager.main(arg);
	

		String uuid = "0efe519d-cddf-412c-a5e0-2e8f14f80edb";
		String token = "EiBmN2UzMzM5ZWFiOGZmZTJkZTg5MTE2NGQ2YjJiOGRiMBjYtte8BQ==";
		String orderSubject = "充值";
		String orderBody = "Body1";
		String amount = "50";
		
		ClientUtilImpl clientUtil = new ClientUtilImpl();
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		sb.append("<appid><![CDATA[wx2421b1c4370ec43b]]></appid>");
		sb.append("<attach><![CDATA[支付测试]]></attach>");
		sb.append(" <bank_type><![CDATA[CFT]]></bank_type>");
		sb.append(" <fee_type><![CDATA[CNY]]></fee_type>");
		sb.append(" <is_subscribe><![CDATA[Y]]></is_subscribe>");
		sb.append("<mch_id><![CDATA[10000100]]></mch_id>");
		sb.append("<nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str>");
		sb.append(" <openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid>");
		sb.append("<out_trade_no><![CDATA[1409811653]]></out_trade_no>");
		sb.append("<result_code><![CDATA[SUCCESS]]></result_code>");
		sb.append(" <return_code><![CDATA[SUCCESS]]></return_code>");
		sb.append("<sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign>");
		sb.append("<sub_mch_id><![CDATA[10000100]]></sub_mch_id>");
		sb.append("<time_end><![CDATA[20140903131540]]></time_end>");
		sb.append("<total_fee>1</total_fee>");
		sb.append("<trade_type><![CDATA[JSAPI]]></trade_type>");
		sb.append("<transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id>");
		sb.append("</xml>");
		
		
		Cookie[] cookies = CommonUtil.createLoginCookieParams(uuid, token);
		String url = "http://localhost:9014/payment/weixin/payNotify";
		String e = postBodyMethod(url, sb.toString().getBytes(), null);

		log.info("type=["+e+"]");
		
		System.exit(0);
	}
	
	public static String postBodyMethod(String urlPath, byte[] body, Cookie[] cookies) throws Exception {
		URL url = new URL(urlPath);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		// 閻犱礁澧介悿鍝緊Output閻忕偟鍋為敓绐栧倽绀媡rue閻炴稏鍔庨妵姘变焊閸℃洖鈻忛柣鈧妽椤掓几rlConnection闁告劖鐟ラ崣鍡涘极閻楀牆绁�

		urlConnection.setDoInput(true);  
		urlConnection.setDoOutput(true);  
		urlConnection.setUseCaches(false);  

		// 閻庤鐭粻鐔奉嚗閸涱厼鏅搁柛蹇嬪劜閺嗙喖骞戦鐐暠闁告劕鎳庨鎰尵鐠囪尙锟界兘鏁嶇仦鎯х亯濞寸媭鍓濋鏇犵磾椤旀槒绀媋pplication/x-www-form-urlencoded缂侇偉顕ч悗锟�

		//urlConnection.setRequestProperty("Content-type", "form-data");
		//urlConnection.setRequestProperty("accept", "application/x-protobuf");
		urlConnection.setRequestMethod("POST"); 
		
		if (cookies != null) {
			StringBuilder sb = new StringBuilder();
			for (Cookie ck : cookies) {
				sb.append(ck.getName()).append('=').append(ck.getValue()).append(";");
			}
			urlConnection.setRequestProperty("Cookie", sb.toString());
		}
		//urlConnection.connect();
		// 鐎电増顨呴崺宀�鎷犻柨瀣勾闁汇劌瀚欢顓㈠礄閻戞銈﹂悗鐢殿攰閽栵拷
		OutputStream out = urlConnection.getOutputStream();
		// 闁硅泛锕ラ弳鐔煎箲椤旂厧鏅搁柛蹇嬪劥椤曨剙效閸屾粍鐣盉ody
		out.write(body);
		out.flush();
		out.close();
		
		// 濞寸姴瀛╁﹢鍥礉閳ヨ櫕鐝ら悹鍥嚙瑜板洭宕鍛畨
		InputStream inputStream = urlConnection.getInputStream();
		String encoding = urlConnection.getContentEncoding();
		String bodyString = IOUtils.toString(inputStream, encoding);

		return bodyString;
	}
}
