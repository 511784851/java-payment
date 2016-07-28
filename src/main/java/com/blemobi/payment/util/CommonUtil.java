package com.blemobi.payment.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author 赵勇<andy.zhao@blemobi.com> 常用函数定义
 */
public class CommonUtil {

	/**
	 * 生成请求uuid和token参数
	 * 
	 * @param uuid
	 *            用户uuid
	 * @param token
	 *            用token
	 * @return List<NameValuePair> uuid和token参数
	 */
	public static List<NameValuePair> createParams(String uuid, String token) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uuid", uuid));
		params.add(new BasicNameValuePair("token", token));
		return params;
	}

	public static Cookie[] createLoginCookieParams(String uuid, String token) {
		Cookie[] cookies = new Cookie[2];
		cookies[0] = new Cookie("uuid", uuid);
		cookies[1] = new Cookie("token", token);
		return cookies;
	}

	/**
	 * 日期格式字符串转换成时间戳
	 * 
	 * @param date
	 *            字符串日期
	 * @param format
	 *            如：yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static long dateTimeStamp(String date_str, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return (sdf.parse(date_str).getTime() / 1000);
	}

	public static String getMapValue(Map map, String key) {
		Object object = map.get(key);
		if (object != null) {
			return object.toString();
		} else {
			return "";
		}
	}
}
