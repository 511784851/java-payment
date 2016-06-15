/*
 *  
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

/**
 * 充保证金任务。
 * 
 * @author 
 * 
 */
public class AddDepositTaskRunable implements Runnable {

    private Task task;
    private DomainResource resource;
    private ChannelAdapterSelector adapterSelector;
    private static final Logger LOG = LoggerFactory.getLogger(AddDepositTaskRunable.class);

    public AddDepositTaskRunable(Task task, DomainResource resource, ChannelAdapterSelector adapterSelector) {
        this.task = task;
        this.resource = resource;
        this.adapterSelector = adapterSelector;
    }

    @Override
    public void run() {
        LOG.info("run AddDepositTask task,task:{}", task);
        // Occupy task
        String appId = task.getAppId();
        String appOrderId = task.getAppOrderId();
        // Find payOrder
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        LOG.debug("get payOrder from databse,payOrder = {}", payOrder);
        if (null == payOrder) {
            LOG.warn("payOrder not found, task stopped,appId:{},appOrderid:{}", task.getAppId(), task.getAppOrderId());
            TaskUtils.refreshEqualInterval(resource, task, Consts.Task.NOTIFY_RETRY_INTERVAL_SECONDS);
            return;
        }

        // If the payorder is fail,we create a normal notify task.
        if (Consts.SC.FAIL.equalsIgnoreCase(payOrder.getStatusCode())) {
            LOG.info("pay is failed,create notify task.payorder={}", payOrder);
            resource.deleteTask(task);
            LOG.info("add deposit result is final, delete task.task={}", task);
            // create a normal notify task.
            createNotifyTask(resource, task);
            return;
        }

        // Only successful payorders are allowed to add deposit.
        if (!Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            LOG.info("payOrder is unsuccessful,please check. payorder = {}", payOrder);
            TaskUtils.refresh(resource, task, Consts.Task.ADD_DEPOSIT_RETRY_INTERVAL_SECONDS);
            return;
        }
        // Check add deposit mark
        String addDepositResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_DEPOSIT);
        if (gbBalanceConsts.ADD_DEPOSIT_SUCCESS.equalsIgnoreCase(addDepositResult)) {
            LOG.info("delete task,adding deposit is done by other thread.appid:{},apporderid:{}", task.getAppId(),
                    task.getAppOrderId());
            resource.deleteTask(task);
            return;
        }

        // As so far,we share an adapter with gb.
        List<AppChInfo> appChInfos = resource.getAppChInfo(payOrder.getAppId(), Consts.Channel.gb,
                Consts.PayMethod.BALANCE);
        if (CollectionUtils.size(appChInfos) != 1) {
            LOG.error("appInfo/appChInfo not found, or get more than one appChInfo,payOrder={}", payOrder);
            return;
        }
        AppChInfo gbChInfo = appChInfos.get(0);

        ChannelIF adapter = adapterSelector.get(Consts.Channel.gb, Consts.PayMethod.BALANCE);
        LOG.info("add gb adapter:{}", adapter.getClass());
        payOrder.setAppChInfo(gbChInfo);
        payOrder = adapter.pay(payOrder);

        addDepositResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_DEPOSIT);

        // Update payorder status
        try {
            // resource.updatePayOrder(payOrder);
            resource.updatePayOrderExt(payOrder);
        } catch (Throwable e) {
            LOG.error("AddgbTaskRunable update payorder fail,add deposit result:{},e:{}", addDepositResult, e);
        }

        // result is final
        if (gbBalanceConsts.ADD_DEPOSIT_SUCCESS.equalsIgnoreCase(addDepositResult)) {
            resource.deleteTask(appId, appOrderId, Task.TYPE_ADD_DEPOSIT);
            LOG.info("delete addgbDeposit task,appid:{},apporderid:{}", task.getAppId(), task.getAppOrderId());
            // create notify task for application
            createNotifyTask(resource, task);
            return;
        }
        TaskUtils.refresh(resource, task, Consts.Task.NOTIFY_RETRY_INTERVAL_SECONDS);
    }

    /**
     * 创建通知任务
     * 
     * @param resource
     * @param task
     */
    public void createNotifyTask(DomainResource resource, Task task) {
        Task notifyTask = resource.getTask(task.getAppId(), task.getAppOrderId(), Task.TYPE_NOTIFY);
        if (notifyTask == null) {
            notifyTask = new Task(task.getAppId(), task.getAppOrderId(), Task.TYPE_NOTIFY, task.getChId(),
                    task.getPayMethod());
        }
        resource.createTask(notifyTask);
    }
}