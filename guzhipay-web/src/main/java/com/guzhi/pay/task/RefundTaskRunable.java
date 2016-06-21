/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.business.PayService;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;

/**
 * 退款定时任务
 * 
 * @author Administrator
 * 
 */
public class RefundTaskRunable implements Runnable {
    private Task task;
    private PayService payService;
    private DomainResource resource;
    private static final Logger logger = LoggerFactory.getLogger(RefundTaskRunable.class);

    public RefundTaskRunable(Task task, PayService payService, DomainResource resource) {
        this.task = task;
        this.payService = payService;
        this.resource = resource;
    }

    @Override
    public void run() {
        logger.info("[RefundTaskRunable]start to run task:{}", task);
        String appId = task.getAppId();
        String appOrderId = task.getAppOrderId();
        // 更新任务为运行状态
        // int updateResult = resource.updateTaskToOccupied(appId, appOrderId,
        // task.getType());
        // if (updateResult != 1) {
        // logger.info("[RefundTaskRunable]task occuppied by other thread, task stopped! ",
        // TraceHelper.getTrace(task));
        // return;
        // }
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        if (payOrder == null) {
            logger.warn("[RefundTaskRunable]payOrder not found, task stopped! ");
            return;
        }
        // 如果已经成功退款，则删除定时任务
        if (Consts.SC.REFUND_SUCCESS.equals(payOrder.getStatusCode())) {
            resource.deleteTask(appId, appOrderId, Task.TYPE_REFUND);
            return;
        }
        // 退款业务逻辑处理
        payService.realRefund(payOrder);
        // 删除定时任务
        resource.deleteTask(appId, appOrderId, Task.TYPE_REFUND);
    }
}
