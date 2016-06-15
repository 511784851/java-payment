/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;

/**
 * http 工具类
 * 
 * @author Administrator
 * 
 */
public class HttpUtils {
    /**
     * 将request中的参数转换成Map
     * 
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Object obj = request.getAttribute("[paramMap]");
        if (obj != null) {
            return (Map<String, String>) obj;
        }
        Map<String, String> paramMap = new HashMap<String, String>();
        Map<String, Object> properties = request.getParameterMap();
        Iterator<Map.Entry<String, Object>> entries = properties.entrySet().iterator();
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            paramMap.put(name, value);
        }
        return paramMap;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getParameterMapForNotify(HttpServletRequest request) {
        Map<String, String> params = new LinkedMap();
        params.put("sign", request.getParameter("sign"));
        params.put("service", request.getParameter("service"));
        params.put("v", request.getParameter("v"));
        params.put("sec_id", request.getParameter("sec_id"));
        params.put("notify_data", request.getParameter("notify_data"));
        return params;
    }

    /**
     * 将map的值转换成字符串
     * 
     * @param map
     * @param encodeFlag 是否编码
     * @return
     */
    public static String map2String(Map<String, String> map) {
        String ret = "";
        if (map == null || map.isEmpty()) {
            return ret;
        }

        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            String name = entry.getKey();
            String value = String.valueOf(entry.getValue());
            if (null == value) {
                value = "";
            }
            ret = ret + name + "=" + value + ";";
        }
        return ret;
    }

    /**
     * 发送响应<br>
     * 成功则返回true，失败则返回false<br>
     * 
     * @param response
     * @param responseContent
     * @return
     */
    public static boolean niceResponse(HttpServletResponse response, String responseContent) {
        boolean done = Boolean.FALSE;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(responseContent);
            pw.flush();
            done = Boolean.TRUE;
        } catch (IOException e) {
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return done;
    }

    /**
     * 拼接URL
     * 
     * @param url
     * @param queryStr
     * @return
     */
    public static String spliceUrl(String url, String queryStr) {
        if (StringUtils.isBlank(url) || StringUtils.isBlank(queryStr)) {
            return StringUtils.defaultString(url);
        }
        queryStr = queryStr.replaceFirst("^[&?]*", "");
        if (StringUtils.endsWith(url, "?")) {
            return url + queryStr;
        }
        String oriQueryStr = URIUtil.getQuery(url);
        if (oriQueryStr != null) {
            return url + "&" + queryStr;
        }
        return url + "?" + queryStr;
    }
}
