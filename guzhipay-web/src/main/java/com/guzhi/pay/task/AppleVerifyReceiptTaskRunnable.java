/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.apple.AppleBalanceConsts;
import com.guzhi.pay.channel.apple.AppleBalanceHelper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.HttpClientHelper;
import com.guzhi.pay.helper.JsonHelper;

/**
 * @author administrator
 * 
 */
public class AppleVerifyReceiptTaskRunnable implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(AppleVerifyReceiptTaskRunnable.class);
    private static final String YYUID = "774263341";
    private Task task;
    private DomainResource resource;

    public AppleVerifyReceiptTaskRunnable(Task task, DomainResource resource) {
        this.task = task;
        this.resource = resource;
    }

    @Override
    public void run() {
        try {
            logger.info("start to run AppleVerifyReceiptTaskRunnable task:{}  ", task);
            String appId = task.getAppId();
            String appOrderId = task.getAppOrderId();
            // int updateResult = resource.updateTaskToOccupied(appId,
            // appOrderId, task.getType());
            // if (updateResult != 1) {
            // logger.info("task occuppied by other thread, task stopped!",
            // TraceHelper.getTrace(task));
            // return;
            // }
            PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
            if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())
                    || Consts.SC.FAIL.equalsIgnoreCase(payOrder.getStatusCode())) {
                return;
            }
            String payUrl = payOrder.getPayUrl();
            String respStr = null;
            String data = AppleBalanceHelper.getReceipt(JsonHelper.fromJson(payOrder.getProdAddiInfo(),
                    AppleBalanceConsts.RECEIPTDATA));
            try {
                // 先去正式环境进行验证
                respStr = HttpClientHelper.sendRequest(payUrl, data, Consts.CHARSET_UTF8, Consts.CHARSET_UTF8);
                logger.info("product envi verify result :{}", respStr);
            } catch (Exception e) {
                logger.error("get exception when product verify receipt, url={} ,e={}", payUrl, e);
                // retry next time
                TaskUtils.refreshEqualInterval(resource, task, Consts.Task.EQUAL_INTERVAL_SECONDS);
                return;
            }
            payOrder = AppleBalanceHelper.updatePayOrderByPay(payOrder, respStr);
            // 如果支付成功，则创建充值YY币任务
            if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
                handlerSuccess(payOrder);
                return;
            }
            // 测试环境只有当yyuid 为774263341 才进行测试
            if (YYUID.equalsIgnoreCase(JsonHelper.fromJson(payOrder.getUserId(), Consts.YYUID))) {
                String testUrl = AppleBalanceHelper.assembleTestUrl(payOrder);
                try {
                    // 测试环境进行验证
                    respStr = HttpClientHelper.sendRequest(testUrl, data, Consts.CHARSET_UTF8, Consts.CHARSET_UTF8);
                    logger.info("test envi verify result :{}", respStr);
                } catch (Exception e) {
                    logger.error("get exception when test verify receipt, url={},e={} ", testUrl, e);
                    // retry next time
                    TaskUtils.refreshEqualInterval(resource, task, Consts.Task.EQUAL_INTERVAL_SECONDS);
                    return;
                }
                payOrder = AppleBalanceHelper.updatePayOrderByPay(payOrder, respStr);
            }
            // 如果再测试环境验证成功，创建充值yy币任务，返回
            if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
                handlerSuccess(payOrder);
                return;
                // 如果再测试环境验证失败，则创建通知任务，通知业务线失败
            } else if (Consts.SC.FAIL.equalsIgnoreCase(payOrder.getStatusCode())) {
                resource.updatePayOrder(payOrder);
                resource.deleteTask(payOrder.getAppId(), payOrder.getAppOrderId(), task.getType());
                resource.createTask(new Task(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_NOTIFY, payOrder
                        .getChId(), payOrder.getPayMethod()));
                return;
            }

            TaskUtils.refreshEqualInterval(resource, task, Consts.Task.EQUAL_INTERVAL_SECONDS);
            // TODO audit log the retry action and we can confirm it works!!
            return;
        } catch (Throwable t) {
            logger.error("AppleVerifyReceiptTaskRunnable Throwable={},e={}", t.getMessage(), t);
            TaskUtils.refreshEqualInterval(resource, task, Consts.Task.EQUAL_INTERVAL_SECONDS);
        }
    }

    /**
     * 验证成功的处理
     * 
     * @param payOrder
     */
    private void handlerSuccess(PayOrder payOrder) {
        resource.updatePayOrder(payOrder);
        resource.deleteTask(payOrder.getAppId(), payOrder.getAppOrderId(), task.getType());
        logger.info("apple verify receipt result is final(normal enviroment), delete task.");
        // 加y币
        if (Consts.YbOper.ADD.equalsIgnoreCase(payOrder.getYyOper())) {
            resource.createTask(new Task(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_ADD_YY, payOrder
                    .getChId(), payOrder.getPayMethod()));
        } else {
            resource.createTask(new Task(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_NOTIFY, payOrder
                    .getChId(), payOrder.getPayMethod()));
        }
        return;
    }
}
