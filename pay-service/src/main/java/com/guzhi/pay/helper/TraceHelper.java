/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;

/**
 * @author administrator
 * 
 */
public class TraceHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TraceHelper.class);

    public static String getTrace(PayOrder payOrder) {

        if (!StringUtils.isEmpty(payOrder.getAppId()) && !StringUtils.isEmpty(payOrder.getAppOrderId())) {
            return "ds:trace:" + payOrder.getAppId() + payOrder.getAppOrderId();
        }
        if (StringUtils.isEmpty(payOrder.getAppId())) {
            LOG.warn("[TraceHelper] appId is empty,payOrder:{}", payOrder);
        }
        if (StringUtils.isEmpty(payOrder.getAppOrderId())) {
            LOG.warn("[TraceHelper] appOrderId is empty,payOrder:{}", payOrder);
        }
        String traceId = (payOrder.getAppId() == null ? "" : payOrder.getAppId())
                + (payOrder.getAppOrderId() == null ? "" : payOrder.getAppOrderId());
        return "ds:trace:" + (StringUtils.isEmpty(traceId) == true ? "0" : traceId);
    }

    public static String getTrace(Map<String, String> params) {

        if (!StringUtils.isEmpty(params.get(Consts.APPID)) && !StringUtils.isEmpty(params.get(Consts.APP_ORDER_ID))) {
            return "ds:trace:" + params.get(Consts.APPID) + params.get(Consts.APP_ORDER_ID);
        }
        if (StringUtils.isEmpty(params.get(Consts.APPID))) {
            LOG.warn("[TraceHelper] appId is empty,params:{}", params);
        }
        if (StringUtils.isEmpty(params.get(Consts.APP_ORDER_ID))) {
            LOG.warn("[TraceHelper] appOrderId is empty,payOrder:{}", params.get(Consts.APP_ORDER_ID));
        }
        String traceId = (params.get(Consts.APPID) == null ? "" : params.get(Consts.APPID))
                + (params.get(Consts.APP_ORDER_ID) == null ? "" : params.get(Consts.APP_ORDER_ID));
        return "ds:trace:" + (StringUtils.isEmpty(traceId) == true ? "0" : traceId);
    }

    public static String getTrace(String appId, String appOrderId) {
        if (!StringUtils.isEmpty(appId) && !StringUtils.isEmpty(appOrderId)) {
            return "ds:trace:" + appId + appOrderId;
        }
        if (StringUtils.isEmpty(appId)) {
            LOG.warn("[TraceHelper] appId is empty,appOrderId:{}", appOrderId);
        }
        if (StringUtils.isEmpty(appOrderId)) {
            LOG.warn("[TraceHelper] appOrderId is empty,appId:{}", appId);
        }
        String traceId = (appId == null ? "" : appId) + (appOrderId == null ? "" : appOrderId);
        return "ds:trace:" + (StringUtils.isEmpty(traceId) == true ? "0" : traceId);
    }

    public static String getTrace(String appId, Map<String, String> data) {
        if (!StringUtils.isEmpty(appId) && !StringUtils.isEmpty(data.get(Consts.APP_ORDER_ID))) {
            return "ds:trace:" + appId + data.get(Consts.APP_ORDER_ID);
        }
        if (StringUtils.isEmpty(appId)) {
            LOG.warn("[TraceHelper] appId is empty,data:{}", data);
        }
        if (StringUtils.isEmpty(data.get(Consts.APP_ORDER_ID))) {
            LOG.warn("[TraceHelper] appOrderId is empty,appId:{},data:{}", appId, data);
        }
        String traceId = (appId == null ? "" : appId)
                + (data.get(Consts.APP_ORDER_ID) == null ? "" : data.get(Consts.APP_ORDER_ID));
        return "ds:trace:" + (StringUtils.isEmpty(traceId) == true ? "0" : traceId);
    }

    public static String getTrace(Task task) {
        if (task == null) {
            return "ds:trace:0";
        }
        if (!StringUtils.isEmpty(task.getAppId()) && !StringUtils.isEmpty(task.getAppOrderId())) {
            return "ds:trace:" + task.getAppId() + task.getAppOrderId();
        }
        if (StringUtils.isEmpty(task.getAppId())) {
            LOG.warn("[TraceHelper] appId is empty,task:{}", task);
        }
        if (StringUtils.isEmpty(task.getAppOrderId())) {
            LOG.warn("[TraceHelper] appOrderId is empty,task", task);
        }
        String traceId = (task.getAppId() == null ? "" : task.getAppId())
                + (task.getAppOrderId() == null ? "" : task.getAppOrderId());
        return "ds:trace:" + (StringUtils.isEmpty(traceId) == true ? "0" : traceId);
    }
}
