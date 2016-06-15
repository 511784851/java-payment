/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;

/**
 * 具有第二次重试功能的httpclient
 * 
 * @author administrator
 * 
 */
public class HttpRetryHelper {

    private static final Logger LOG = LoggerFactory.getLogger(HttpRetryHelper.class);

    private static ThreadPoolExecutor executor;
    static {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
        // 为避免线程池数量过多，允许core线程timeout
        executor.setKeepAliveTime(10, TimeUnit.SECONDS);
        executor.allowCoreThreadTimeOut(true);
    }

    private static void sendRequest(final String targetUrl, final String requestBody, final CountDownLatch cdl,
            final PayInfo info) {

        Runnable task = new Runnable() {
            public void run() {
                String responseBody = StringUtils.EMPTY;
                try {
                    if (StringUtils.isBlank(requestBody)) {
                        responseBody = HttpClientHelper.sendRequest(targetUrl, Consts.CHARSET_UTF8);
                    } else {
                        responseBody = HttpClientHelper.sendRequest(targetUrl, requestBody, Consts.CHARSET_UTF8,
                                Consts.CHARSET_UTF8);
                    }
                    info.payResponse = responseBody;
                } catch (Exception e) {
                    // don't care
                } finally {
                    cdl.countDown();
                }
            }
        };
        executor.execute(task);
    }

    /**
     * 
     * @param targetUrl
     * @param timeout 如果timeout毫秒内未返回,发起第二次请求,第二次请求如果也超时10秒，则抛出连接异常
     * @return
     */
    public static String sendRequest(String targetUrl, int timeout) {
        return sendRequest(targetUrl, null, timeout, 10000);
    }

    /**
     * 
     * @param targetUrl
     * @param firstTimeout 如果firstTimeout 毫秒内未返回,发起第二次请求
     * @param secondTimeout 第二次请求如果也超时secondTimeout毫秒，则抛出连接异常
     * @return
     */
    public static String sendRequest(String targetUrl, int firstTimeout, int secondTimeout) {
        return sendRequest(targetUrl, null, firstTimeout, secondTimeout);
    }

    /**
     * 
     * @param targetUrl
     * @param requestBody
     * @param timeout 如果timeout毫秒内未返回,发起第二次请求,第二次请求如果也超时10秒，则抛出连接异常
     * @return
     */
    public static String sendRequest(String targetUrl, String requestBody, int timeout) {
        return sendRequest(targetUrl, requestBody, timeout, 10000);
    }

    public static String sendRequest(String targetUrl, String requestBody, int firstTimeout, int secondTimeout) {
        String result = sendHelper(targetUrl, requestBody, firstTimeout, secondTimeout);
        LOG.info("[sendRequest] targetUrl:{}, requestBody:{}, firstTimeout:{},secondTimeout:{}, result:{}", targetUrl,
                requestBody, firstTimeout, secondTimeout, result);
        if (StringUtils.isBlank(result)) {
            throw new PayException(Consts.SC.CONN_ERROR, "第三方渠道网络连接异常");
        }
        return result;
    }

    /**
     * 第一次请求timeout 毫秒内还未返回，发起第二次请求;
     * 
     * 两次请求，只要有结果返回，则结束！两次请求在10秒内都没有返回，则抛出异常CODE_CONN_ERROR异常
     * 
     * @param targetUrl
     * @param requestBody
     * @param firstTimeout
     * @param sencondTimeout
     * @return
     */
    public static String sendHelper(String targetUrl, String requestBody, int firstTimeout, int secondTimeout) {

        LOG.info("[sendRequest] targetUrl:{}, requestBody:{}, executor.activing.count:{}", targetUrl, requestBody,
                executor.getActiveCount());
        CountDownLatch cdl = new CountDownLatch(1);
        PayInfo info = new PayInfo();

        sendRequest(targetUrl, requestBody, cdl, info);
        try {
            boolean wait = cdl.await(firstTimeout, TimeUnit.MILLISECONDS);
            // 如果firstTimeout内返回结果
            if (wait) {
                return info.payResponse;
            }
        } catch (InterruptedException e) {
        }

        // 如果firstTimeout内请求没有返回，发起第二次请求
        sendRequest(targetUrl, requestBody, cdl, info);
        try {
            boolean wait = cdl.await(secondTimeout, TimeUnit.MILLISECONDS);
            // 如果secondTimeout毫秒内返回结果(第一次和第二次不管是哪次返回)
            if (wait) {
                LOG.info("[sendRequest] return after second request");
                return info.payResponse;
            }
        } catch (InterruptedException e) {
        }
        throw new PayException(Consts.SC.CONN_ERROR, "与第三方渠道网络连接异常");
    }

    private static class PayInfo {
        public String payResponse;
    }

    public static void main(String[] args) {
        System.out.println(HttpRetryHelper.sendRequest("http://www.99bill.com/gateway/getOrderTimestamp.htm", "ss",
                200, 10000));
    }

}
