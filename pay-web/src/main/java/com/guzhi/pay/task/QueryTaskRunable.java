/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.business.PayService;
import com.guzhi.pay.channel.zfb.ZfbConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;

/**
 * 处理查询任务
 * 
 * @author administrator
 * @author administrator
 */
public class QueryTaskRunable implements Runnable {
    private Task task;
    private PayService payService;
    private DomainResource resource;
    private static final Logger logger = LoggerFactory.getLogger(QueryTaskRunable.class);

    public QueryTaskRunable(PayService payService, DomainResource resource, Task task) {
        super();
        this.payService = payService;
        this.resource = resource;
        this.task = task;
    }

    public void run() {
        try {
            logger.info("[QueryTaskRunable]start to run task:{}", task);

            // occupy task
            String appId = task.getAppId();
            String appOrderId = task.getAppOrderId();

            // find payOrder
            PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
            if (payOrder == null) {
                logger.warn("[QueryTaskRunable]payOrder not found, task stopped! ");
                resource.deleteTask(task);
                return;
            }

            payOrder = payService.query(payOrder); // 里面已经包含发起异步通知的逻辑

            // result is final
            String statusCode = (payOrder == null) ? null : payOrder.getStatusCode();
            if (Consts.SC.SUCCESS.equals(statusCode) || Consts.SC.FAIL.equals(statusCode)
                    || Consts.SC.CARD_ERROR.equals(statusCode) || Consts.SC.REFUND_SUCCESS.equals(statusCode)
                    || ZfbConsts.OVERED_REFUND.equalsIgnoreCase(statusCode) || Consts.SC.RISK_ERROR.equals(statusCode)) {
                logger.info("query result is final, delete task.");
                resource.deleteTask(task);
                return;
            }

            logger.info("QueryTaskRunable retryTimes appid:{},apporderid:{},retryTimes:{}", task.getAppId(),
                    task.getAppOrderId(), task.getRetryTimes());
            dealTask(task);
        } catch (Throwable t) {
            logger.error("run QueryTaskRunable error msg:{},task:{}", t.getMessage(), task, t);
            dealTask(task);
        } finally {
            if (task.getCdl() != null) {
                task.getCdl().countDown();
            }
        }
    }

    private final static int QUERY_TASK_MAX_TIMES = 5;

    private void dealTask(Task task) {
        if (task.getRetryTimes() >= QUERY_TASK_MAX_TIMES) {
            resource.deleteTask(task);
        } else {
            TaskUtils.refresh(resource, task, Consts.Task.QUERY_RETRY_INTERVAL_SECONDS);
        }
    }
}
