/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.zfb.ZfbHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * 处理通知任务
 * 
 * @author administrator
 * @author administrator
 */
public class NotifyTaskRunable implements Runnable {
    private Task task;
    private DomainResource resource;
    private static Logger logger = LoggerFactory.getLogger(NotifyTaskRunable.class);

    public NotifyTaskRunable(DomainResource resource, Task task) {
        this.resource = resource;
        this.task = task;
    }

    public void run() {
        logger.info("[NotifyTaskRunable]start to run task:{}", task);

        // occupy task
        String appId = task.getAppId();
        String appOrderId = task.getAppOrderId();
        // int updateResult = resource.updateTaskToOccupied(appId, appOrderId,
        // task.getType());
        // if (updateResult != 1) {
        // logger.info("[NotifyTaskRunable]task occuppied by other thread, task stopped!",
        // TraceHelper.getTrace(task));
        // return;
        // }

        // find payOrder
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        AppInfo appInfo = resource.getAppInfo(appId);
        String notifyUrl = payOrder.getNotifyUrl();
        payOrder.setAppInfo(appInfo);
        if (payOrder == null || appInfo == null || StringUtils.isBlank(notifyUrl)) {
            logger.warn("payOrder/appInfo not found, or notifyUrl empty, task stopped! ");
            return;
        }

        // perform notify
        String targetUrl = null;
        String respStr = null;
        try {
            targetUrl = notifyUrl + "?" + ZfbHelper.assemblePayResultQueryStr(payOrder);
            logger.info("[NotifyTaskRunable]created notify url={}", targetUrl);
            respStr = HttpClientHelper.sendRequest(targetUrl);
            logger.info("[NotifyTaskRunable]respStr {}", respStr, TraceHelper.getTrace(task));
        } catch (Exception e) {
            logger.error("[NotifyTaskRunable]get exception when notify app, url={},e={} ", targetUrl, e.getMessage());
        }

        // notify success
        if (Consts.SC.SUCCESS_NOTIFY.equalsIgnoreCase(StringUtils.trim(respStr))) {
            logger.info("[NotifyTaskRunable]notify success, delete task.");
            handPayTime(payOrder);
            resource.deleteTask(appId, appOrderId, Task.TYPE_NOTIFY);
            return;
        }

        /*
         * TODO for some kind of error,such as 500 etc. should trigger a email
         * notification
         * to let the service owner know their problem in time here.
         */

        TaskUtils.refresh(resource, task, Consts.Task.NOTIFY_RETRY_INTERVAL_SECONDS);
        // TODO audit log the retry action and we can confirm it works!!
        return;
    }

    // 10分钟
    private static final int MAX_FINISH_TIME = 600000;

    /**
     * 整个订单处理时间
     * 
     * @param payOrder
     */
    private void handPayTime(PayOrder payOrder) {
        try {
            Date submitTime = TimeHelper.strToDate(payOrder.getSubmitTime(), TimeHelper.TIME_FORMATE);
            Date lastUpdateTime = TimeHelper.strToDate(payOrder.getLastUpdateTime(), TimeHelper.TIME_FORMATE1);
            long sub1 = TimeHelper.dateSubDate(submitTime, lastUpdateTime);
            long sub2 = TimeHelper.dateSubDate(lastUpdateTime, new Date());
            logger.info("time static,appid:{},apporderid:{},pay time:{},notify time:{}", payOrder.getAppId(),
                    payOrder.getAppOrderId(), sub1, sub2);
            if (sub2 > MAX_FINISH_TIME) {
                logger.info(
                        "[notify] FINISH_TIME_TOO_LONG finish time >{},appid:{},apporderid:{},chid:{},paymethod:{},finish time:{}",
                        MAX_FINISH_TIME, payOrder.getAppId(), payOrder.getAppOrderId(), payOrder.getChId(),
                        payOrder.getPayMethod(), sub1 + sub2);
            }
        } catch (Throwable t) {
            logger.error("handPayTime error payorder :{}", payOrder);
        }

    }
}
