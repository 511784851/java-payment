package com.guzhi.pay.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.guzhi.pay.common.Consts;

/**
 * Small help utilities for URL manipulation.
 * 
 * @author administrator
 */
public class UrlHelper {
    private static final String EQ = "=";
    private static final String AMP = "&";

    /**
     * 确保两头没有空白，且最后一个字符不是"/"
     */
    public static String removeLastSep(String url) {
        String url2 = url.trim();
        if (url2.endsWith("/")) {
            return url2.substring(0, url2.length() - 1);
        }
        return url2;
    }

    /**
     * 确保两头没有空白，且头一个字符不是"/"
     */
    public static String removeFirstSep(String path) {
        String path2 = path.trim();
        if (path2.startsWith("/")) {
            return path2.substring(1, path2.length());
        }
        return path2;
    }

    /**
     * 确保最后有一个问号
     * 
     * @param path
     * @return
     */
    public static String addQuestionMark(String path) {
        String path2 = path.trim();
        if (!path2.endsWith("?")) {
            return path2 + "?";
        }
        return path2;
    }

    /**
     * 组装查询字符串
     * 
     * @param map 请求参数
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String assembleQueryStr(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (String key : map.keySet()) {
            if (StringUtils.isBlank(map.get(key))) {
                continue;
            }
            try {
                String ecodedStr = URLEncoder.encode(map.get(key), Consts.CHARSET_UTF8);
                builder.append(key).append(EQ).append(ecodedStr).append(AMP);
            } catch (UnsupportedEncodingException e) {
            }
        }

        String queryStr = builder.toString().replaceAll("&$", ""); // remove last "&"
        return queryStr;
    }

}
