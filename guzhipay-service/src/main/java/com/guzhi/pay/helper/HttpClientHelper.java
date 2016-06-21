package com.guzhi.pay.helper;

import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;

/**
 * 用于处理REST HTTP GET/POST请求与回复。
 */
public class HttpClientHelper {

    private static Logger log = LoggerFactory.getLogger(HttpClientHelper.class);

    private static HttpClient httpClient = HttpClientFactory.getHttpClient();

    public static String sendRequest(String requestUrl) {
        return sendRequest(false, requestUrl, null, null, Consts.CHARSET_UTF8);
    }

    public static String sendRequest(String requestUrl, String respEncoding) {
        return sendRequest(false, requestUrl, null, null, respEncoding);
    }

    public static String sendRequest(String requestUrl, String requestBody, String requestEncoding,
            String respEncoding) {
        return sendRequest(true, requestUrl, requestBody, requestEncoding, respEncoding);
    }

    /**
     * 用于发送HTTP的POST/Get请求，接收并解析响应。
     * 
     * @param isPost 是否Post
     * @param reqUrl
     * @param requestBody
     * @param requestEncoding
     * @param respEncoding
     * @return
     */
    private static String sendRequest(boolean isPost, String reqUrl, String requestBody, String requestEncoding,
            String respEncoding) {
        log.info("Sending out request with URL: {}", reqUrl);

        // 构造request
        HttpMethod httpRequest = null;

        if (isPost) {
            httpRequest = new PostMethod(reqUrl);
            StringEntity entity = null;
            try {
                entity = new StringEntity(requestBody, requestEncoding);
            } catch (UnsupportedEncodingException e) {
                throw new PayException(Consts.SC.DATA_FORMAT_ERROR, "Failed to create http request!", null, e);
            }

            ((HttpPost) httpRequest).setEntity(entity);
            ((HttpPost) httpRequest).setHeader("Content-Type", "text/html;charset=" + requestEncoding);
        } else {
            httpRequest = new GetMethod(reqUrl);
        }

        // 发送请求，获取Response
        String response = null;
        try {
            TimeCostHelper.suspend();
            int result = httpClient.executeMethod(httpRequest);
            if (result == 200) {
                response = httpRequest.getResponseBodyAsString();
            }
            TimeCostHelper.resume();
        } catch (Exception e) {
            TimeCostHelper.resume();
            log.warn("[sendRequest] connection was aborted,httpRequest:{},e={},reqUrl:{},requestBody{}", httpRequest,
                    e.getMessage(), reqUrl, requestBody);
            throw new PayException(Consts.SC.CONN_ERROR, "与第三方渠道网络连接异常", null, e);
        }

        // 检查Response内容不为空
        if (response == null) {
            throw new PayException(Consts.SC.CONN_ERROR, "response or its enclosed entity empty, url: " + reqUrl);
        }

        return response;
    }
}