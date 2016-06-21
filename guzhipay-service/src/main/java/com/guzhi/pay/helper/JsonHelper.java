/**
 * Copyright (c) 2011 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;

/**
 * 用于实现PayOrder与JSON字符串之间的转换。
 * 
 * @author administrator
 */
@SuppressWarnings("unchecked")
public class JsonHelper {

    private static ObjectMapper mapper = new ObjectMapper();
    static {
        // 忽略不存在的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    private static Logger logger = LoggerFactory.getLogger(JsonHelper.class);

    /**
     * 通过JSON字符串生成对象
     * 
     * @param json JSON字符串
     * @param type 返回值的类型
     * @return 如果能够封装为指定对象，则返回该值，否则返回null
     */
    public static <T> T fromJson(String json, Class<T> type) {
        T result = null;
        try {
            if (StringUtils.isNotBlank(json)) {
                result = mapper.readValue(json, type);
            }
            return result;
        } catch (Exception e) {
            throw new PayException(Consts.SC.DATA_FORMAT_ERROR, "Error converting json to obj: " + json, null, e);
        }
    }

    /**
     * 生成JSON字符串.生成字符串会自动进行HTML转义及不可直接被解释.
     * 
     * @param obj 对象实例
     * @return 返回生成的字符串
     */
    public static String toJson(Object obj) {
        String result = null;
        try {
            if (obj != null) {
                result = mapper.writeValueAsString(obj);
            }
            return result;
        } catch (Exception e) {
            throw new PayException(Consts.SC.DATA_FORMAT_ERROR, "Error converting obj to json: " + obj, null, e);
        }
    }

    /**
     * 简单地组装一个json对象
     * 
     * @param keyVals 键值对，偶数序系列(0,2,4...)是键名，必须是字符串，
     *            基数序系列(1,3,5...)是值，可为空
     * @return
     */
    public static String formJson(Object... keyVals) {
        if (keyVals == null || keyVals.length == 0)
            return null;

        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < keyVals.length; i += 2) {
            map.put((String) keyVals[i], i + 1 == keyVals.length ? null : keyVals[i + 1]);
        }
        return toJson(map);
    }

    /**
     * 将请求的JSON字符串转换成PayOrder对象，利用@JsonView({ PayOrder.ReqView.class })
     * 
     * @param reqJson
     * @param viewClass
     * @return PayOrder对象（注：appId字段需要额外设置）
     */
    public static PayOrder reqJsonToPayOrder(String reqJson, @SuppressWarnings("rawtypes") Class viewClass) {
        if (!PayOrder.PayReqView.class.equals(viewClass) && !PayOrder.QueryReqView.class.equals(viewClass)
                && !PayOrder.RefundReqView.class.equals(viewClass)) {
            String msg = String.format("Can not parse json (used wrong view), view=%s, reqJson=%s", viewClass, reqJson);
            throw new PayException(Consts.SC.REQ_ERROR, msg, null, null);
        }

        try {
            PayOrder payOrder = mapper.readerWithView(viewClass).withType(PayOrder.class).readValue(reqJson);
            logger.debug("parsing json to payOrder, viewClass={}, json={}, payOrder={}", viewClass, reqJson, payOrder);
            return payOrder;
        } catch (Exception e) {
            throw new PayException(Consts.SC.REQ_ERROR, "Error parsing json:" + reqJson, null, e);
        }
    }

    /**
     * 将PayOrder对象转化成响应的JSON对象，利用@JsonView({ PayOrder.RespView.class })
     * 
     * @param payOrder
     * @return Json响应字符串
     */
    public static String payOrderToRespJson(PayOrder payOrder) {

        // 注意：对于错误的返回，appId和appOrderId都有可能为空，故不能检查它们

        // 始终确保StatusCode和StatusMsg不为空
        if (payOrder == null) {
            payOrder = new PayOrder();
        }
        if (StringUtils.isBlank(payOrder.getStatusCode())) {
            payOrder.setStatusCode(Consts.SC.INTERNAL_ERROR);
        }
        if (StringUtils.isBlank(payOrder.getStatusMsg())) {
            // payOrder.setStatusMsg("Try to marshal a null payOrder!");
            payOrder.setStatusMsg(payOrder.getStatusCode());
        }

        try {
            String jsonStr = mapper.writerWithView(PayOrder.RespView.class).writeValueAsString(payOrder);
            logger.debug("marshal payOrder to json, json={}, payOrder={}", jsonStr, payOrder);
            return jsonStr;
        } catch (Exception e) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "Error marshalling payOrder:" + payOrder, payOrder, e);
        }
    }

    /**
     * 
     * @param json JSON字符串 以{key:value} 形式
     * @param key key
     * @return value
     */

    public static String fromJson(String json, String key) {

        Map<String, String> map = null;
        try {
            map = fromJson(json, Map.class);
        } catch (Throwable e) {
            return null;
        }
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    /**
     * 
     * @param json JSON字符串 以{key:value} 形式
     * @param key key
     * @return value
     */

    public static String putJson(String json, String key, String value) {

        Map<String, String> map = null;
        // json为空,则初始化该map
        if (StringUtils.isEmpty(json)) {
            map = new HashMap<String, String>();
        } else {
            try {
                map = fromJson(json, Map.class);
            } catch (Throwable e) {
                // 异常,则返回空
                return null;
            }
        }
        if (map != null && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            map.put(key, value);
            return toJson(map);
        }
        return null;
    }

    public static void main(String[] args) {
        // System.out.println(putJson("", "1", "2"));
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\"").append("yyuid").append("\":\"13778000\"").append("}");
        // String a = {"product":"TEST","encryptAddYbKey":"KEY123456789"}
        System.out.println(sb.toString());
        System.out.println(fromJson(sb.toString(), "yyuid"));
    }
}
