/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.rest
 *
 *    Filename:    HttpUtils.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月7日 下午5:12:50
 *
 *    Revision:
 *
 *    2017年3月7日 下午5:12:50
 *
 *****************************************************************/
package com.blemobi.payment.rest;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

/**
 * @ClassName HttpUtils
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月7日 下午5:12:50
 * @version 1.0.0
 */
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import okhttp3.FormBody;
 
/**
 * Created by yinfx on 15-9-16.
 */
public class HttpUtils {
    public static final MediaType PROTO_BUF = MediaType.parse("application/x-protobuf; charset=utf-8");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType XML = MediaType.parse("application/xml; charset=utf-8");
    private static HttpUtils instance;
    private OkHttpClient client;
 
    private HttpUtils() {
        client = new OkHttpClient();
        client.setConnectTimeout(20, TimeUnit.SECONDS);//设置请求超时，20s
        client.setReadTimeout(20, TimeUnit.SECONDS);//设置读取超时，20s
        client.setWriteTimeout(20, TimeUnit.SECONDS);//设置写入超时，20s
    }
 
    public synchronized static HttpUtils getInstance() {
        return instance == null ? instance = new HttpUtils() : instance;
    }

    
    public String post(String url, MediaType mediaType, byte[] data, Map<String, String> cookies) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        if(cookies != null && cookies.size() > 0){
            List<Cookie> cookieList = new ArrayList<Cookie>();
            for(Entry<String, String> entry : cookies.entrySet()){
                Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
                cookieList.add(cookie);
            }
            builder = builder.header("Cookie", cookieHeader(cookieList));
        }
        Request request = builder.post(RequestBody.create(mediaType, data)).build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            System.out.println(response.body().toString());
        }else{
            System.err.println("error ");
        }
        
        return response.body().string();
    }
    
    private static String cookieHeader(List<Cookie> cookies) {
        StringBuilder cookieHeader = new StringBuilder();
        for (int i = 0, size = cookies.size(); i < size; i++) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.getName()).append('=').append(cookie.getValue());
        }
        return cookieHeader.toString();
    }
    
 
    /**
     * 提交GET请求
     *
     * @param url 地址
     * @return
     * @throws IOException
     */
    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
