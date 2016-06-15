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
 * 充频道保证金任务。
 * 
 * @author 
 * 
 */
public class AddChannelDepositTaskRunnable implements Runnable {
    private Task task;
    private DomainResource resource;
    private ChannelAdapterSelector adapterSelector;
    private static final Logger LOG = LoggerFactory.getLogger(AddChannelDepositTaskRunnable.class);

    public AddChannelDepositTaskRunnable(Task task, DomainResource resource, ChannelAdapterSelector adapterSelector) {
        this.task = task;
        this.resource = resource;
        this.adapterSelector = adapterSelector;
    }

    @Override
    public void run() {
        LOG.info("start run add channel deposit task,task:{}", task);
        String appId = task.getAppId();
        String appOrderId = task.getAppOrderId();
        PayOrder payOrder = resource.getPayOrder(appId, appOrderId);
        if (payOrder == null) {
            LOG.error("get no payorder from database,appid:{},apporderid:{}", appId, appOrderId);
            return;
        }
        String result = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_CHANNEL_DEPOSIT);
        if (gbBalanceConsts.ADD_CHANNEL_DEPOSIT_SUCCESS.equalsIgnoreCase(result)) {
            LOG.info("not need to run a success task,delete task,task:{},ext:{}", task, payOrder.getExt());
            resource.deleteTask(appId, appOrderId, task.getType());
            Task notifyTask = resource.getTask(appId, appOrderId, Task.TYPE_NOTIFY);
            if (notifyTask == null) {
                LOG.info("create a notify task,appid:{},apporderid:{}", appId, appOrderId);
                resource.createTask(new Task(appId, appOrderId, Task.TYPE_NOTIFY, payOrder.getChId(), payOrder
                        .getPayMethod()));
            }
            return;
        }
        if (!Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            LOG.info("unsuccess payorder,refresh the task,task:{}", task);
            TaskUtils.refresh(resource, task, Consts.Task.ADD_DEPOSIT_RETRY_INTERVAL_SECONDS);
            return;
        }
        List<AppChInfo> appChInfos = resource.getAppChInfo(appId, Consts.Channel.gb, Consts.PayMethod.BALANCE);
        if (CollectionUtils.size(appChInfos) != 1) {
            LOG.error("return,get no gb balance AppChInfo for task,task:{}", task);
            TaskUtils.refresh(resource, task, Consts.Task.ADD_DEPOSIT_RETRY_INTERVAL_SECONDS);
            return;
        }
        AppChInfo gbChInfo = appChInfos.get(0);
        ChannelIF gbAdapter = adapterSelector.get(Consts.Channel.gb, Consts.PayMethod.BALANCE);
        payOrder.setAppChInfo(gbChInfo);
        LOG.info("start add channel deposit,appid:{},apporderid:{}", appId, appOrderId);
        payOrder = gbAdapter.pay(payOrder);
        String addResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_CHANNEL_DEPOSIT);
        // resource.updatePayOrder(payOrder);
        try {
            resource.updatePayOrderExt(payOrder);
        } catch (Throwable t) {
            LOG.error("[addChannelDeposit] change ext error,appid:{},apporderid:{},newext:{},e:{}", appId, appOrderId,
                    payOrder.getExt(), t.getMessage());
        }
        if (gbBalanceConsts.ADD_CHANNEL_DEPOSIT_SUCCESS.equalsIgnoreCase(addResult)) {
            LOG.info("add channel deposit success,delete task,task:{},payOrder:{}", task, payOrder);
            resource.deleteTask(appId, appOrderId, task.getType());
            Task notifyTask = resource.getTask(appId, appOrderId, Task.TYPE_NOTIFY);
            if (notifyTask == null) {
                LOG.info("create a notify task,appId:{},apporderid:{}", appId, appOrderId);
                resource.createTask(new Task(appId, appOrderId, Task.TYPE_NOTIFY, payOrder.getChId(), payOrder
                        .getPayMethod()));
            }
            return;
        }
        LOG.info("add channel deposit fail,refresh the task,appid:{},apporderid:{}", appId, appOrderId);
        TaskUtils.refresh(resource, task, Consts.Task.ADD_DEPOSIT_RETRY_INTERVAL_SECONDS);
    }
}
