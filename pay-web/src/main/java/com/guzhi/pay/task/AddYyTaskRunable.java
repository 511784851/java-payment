/*
 * Copyright (c) 2013 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.ChannelAdapterSelector;
import com.guzhi.pay.channel.ChannelIF;
import com.guzhi.pay.channel.gb.gbBalanceConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppChInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.TraceHelper;

/**
 * @author Administrator
 *         gb币充值定时任务
 */
public class AddgbTaskRunable implements Runnable {

    private Task task;
    private DomainResource resource;
    private ChannelAdapterSelector adapterSelector;
    private static final Logger logger = LoggerFactory.getLogger(AddgbTaskRunable.class);

    public AddgbTaskRunable(Task task, DomainResource resource, ChannelAdapterSelector adapterSelector) {
        this.task = task;
        this.resource = resource;
        this.adapterSelector = adapterSelector;
    }

    @Override
    public void run() {
        logger.info("start to run AddgbTaskRunable task:{}", task);

        // occupy task
        String appId = task.getAppId();
        String appOrderId = task.getAppOrderId();
        // int updateResult = resource.updateTaskToOccupied(appId, appOrderId,
        // task.getType());
        // if (updateResult != 1) {
        // logger.info("task occuppied by other thread, task stopped! updateResult={}",
        // updateResult,
        // TraceHelper.getTrace(task));
        // return;
        // }
        // find payOrder
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        logger.debug("pay order = {}", payOrder, TraceHelper.getTrace(task));

        if (payOrder == null) {
            logger.warn("payOrder not found, task stopped!");
            return;
        }

        // If the payorder is fail,we create a normal notify task.
        if (Consts.SC.FAIL.equalsIgnoreCase(payOrder.getStatusCode())) {
            logger.info("pay is failed,create notify task.payorder={}", payOrder);
            resource.deleteTask(appId, appOrderId, Task.TYPE_ADD_GB);
            logger.info("addgb result is final, delete task.task={}", task);
            // 创建notify业务线的定时任务
            task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_NOTIFY);
            if (task == null) {
                resource.createTask(new Task(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_NOTIFY, payOrder
                        .getChId(), payOrder.getPayMethod()));
            }
            return;
        }

        // 查看到订单状态是否支付成功,订单支付不成功不允许充gb币
        if (!Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            logger.info("pay is not success,please check. payorder = {}", payOrder);
            TaskUtils.refresh(resource, task, Consts.Task.ADD_gb_RETRY_INTERVAL_SECONDS);
            return;
        }
        // 查看该订单是否已经充值gb币成功，已经成功的，不允许继续充值
        String addgbResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_gb);

        // gb 币或者保证经已经充值成功则删除该定时任务
        if (gbBalanceConsts.ADD_gb_SUCCESS.equalsIgnoreCase(addgbResult)
                || gbBalanceConsts.ADD_DEPOSIT_SUCCESS.equalsIgnoreCase(addgbResult)) {
            logger.info("addgb result is final,addgb success, delete task.");
            resource.deleteTask(appId, appOrderId, Task.TYPE_ADD_GB);
            return;
        }

        // 取到gb的渠道消息
        List<AppChInfo> appChInfos = resource.getAppChInfo(payOrder.getAppId(), Consts.Channel.gb,
                Consts.PayMethod.BALANCE);
        if (CollectionUtils.size(appChInfos) != 1) {
            String msg = "appInfo/appChInfo not found, or get more than one appChInfo!";
            logger.error(" payOrder={},msg={}", payOrder, msg);
            return;
        }
        AppChInfo gbChInfo = appChInfos.get(0);

        ChannelIF adapter = adapterSelector.get(Consts.Channel.gb, Consts.PayMethod.BALANCE);
        logger.info("add gb adapter:{}", adapter.getClass());
        payOrder.setAppChInfo(gbChInfo);
        payOrder = adapter.pay(payOrder);

        addgbResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_gb);

        // 更新订单的ext字段信息状态
        try {
            // resource.updatePayOrder(payOrder);
            int result = resource.updatePayOrderExt(payOrder);
            if (result <= 0) {
                logger.error("update payOrder ext failed, appId:{},appOrderId:{},ext:{}", appId, appOrderId,
                        payOrder.getExt());
            }
        } catch (Throwable e) {
            logger.error("AddgbTaskRunable update payorder fail,addgbResult:{},e:{}", addgbResult, e);
        }

        // result is final
        if (gbBalanceConsts.ADD_gb_SUCCESS.equalsIgnoreCase(addgbResult)) {
            resource.deleteTask(appId, appOrderId, Task.TYPE_ADD_GB);
            logger.info("addgb result is final, delete task.");
            // 创建notify业务线的定时任务
            task = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_NOTIFY);
            if (task == null) {
                resource.createTask(new Task(payOrder.getAppId(), payOrder.getAppOrderId(), Task.TYPE_NOTIFY, payOrder
                        .getChId(), payOrder.getPayMethod()));
            }
            return;
        }

        TaskUtils.refresh(resource, task, Consts.Task.NOTIFY_RETRY_INTERVAL_SECONDS);
        return;

    }
}
