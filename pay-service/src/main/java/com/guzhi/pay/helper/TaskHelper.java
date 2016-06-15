/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.gb.gbBalanceConsts;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.helper.JsonHelper;

/**
 * 与业务紧耦合的任务帮助类。
 * 
 * @author 
 * 
 */
public class TaskHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TaskHelper.class);

    private static final String[] VALIDTASKTYPES = new String[] { Task.TYPE_ADD_CHANNEL_DEPOSIT, Task.TYPE_ADD_DEPOSIT,
            Task.TYPE_ADD_GB, Task.TYPE_NOTIFY, Task.TYPE_PAY_APPLE, Task.TYPE_PAY_ASYNC, Task.TYPE_PAY_JWJK,
            Task.TYPE_PAY_KQSZX, Task.TYPE_PAY_SZFCARD, Task.TYPE_PAY_THYKT, Task.TYPE_PAY_YEEPAYCARD, Task.TYPE_QUERY,
            Task.TYPE_REFUND };

    private TaskHelper() {
    }

    /**
     * 订单支付之后，创建任务.
     * <ol>
     * <li>创建添加gb任务</li>
     * <li>创建添加保证金任务</li>
     * <li>创建添加频道保证金任务</li>
     * <li>创建通知任务</li>
     * </ol>
     * 
     * @param resource
     * @param payOrder
     */
    public static void createAfterPaySuccessTask(DomainResource resource, PayOrder payOrder) {
        if (payOrder == null) {
            LOG.warn("[return.createTask] payorder is empty.");
            return;
        }
        if (!Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
            return;
        }

        if (StringUtils.isEmpty(payOrder.getgbOper())) {
            createNotifyTask(resource, payOrder);
            return;
        }

        // 当gboper 参数不为空，并且订单状态是成功时才充gb币或者保证金
        String addgbResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_gb);
        String addDepositResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_DEPOSIT);
        String addChDepositResult = JsonHelper.fromJson(payOrder.getExt(), gbBalanceConsts.ADD_CHANNEL_DEPOSIT);

        // 创建添加G币任务
        if (Consts.gbOper.ADD.equalsIgnoreCase(payOrder.getgbOper())
                && !gbBalanceConsts.ADD_gb_SUCCESS.equalsIgnoreCase(addgbResult)) {
            createSpecifiedTask(resource, payOrder, Task.TYPE_ADD_GB);
        } else if (Consts.gbOper.ADD_DEPOSIT.equalsIgnoreCase(payOrder.getgbOper())
                && !gbBalanceConsts.ADD_DEPOSIT_SUCCESS.equalsIgnoreCase(addDepositResult)) {
            // 创建添加视频保证金任务
            createSpecifiedTask(resource, payOrder, Task.TYPE_ADD_DEPOSIT);
        } else if (Consts.gbOper.ADD_CHANNEL_DEPOSIT.equalsIgnoreCase(payOrder.getgbOper())
                && !gbBalanceConsts.ADD_CHANNEL_DEPOSIT_SUCCESS.equalsIgnoreCase(addChDepositResult)) {
            // 创建添加频道保证金任务
            createSpecifiedTask(resource, payOrder, Task.TYPE_ADD_CHANNEL_DEPOSIT);
        } else {
            // 创建通知任务
            createNotifyTask(resource, payOrder);
        }
    }

    /**
     * 创建通知任务.<br>
     * 如果通知任务已经存在，那么不再创建通知任务.
     * 
     * @param resource
     * @param payOrder
     */
    public static void createNotifyTask(DomainResource resource, PayOrder payOrder) {
        createSpecifiedTask(resource, payOrder, Task.TYPE_NOTIFY);
    }

    /**
     * 创建指定类型的任务. <br>
     * 如果该任务已经存在，那么不再创建.
     * 
     * @param resource
     * @param payOrder
     * @param taskType
     */
    public static void createSpecifiedTask(DomainResource resource, PayOrder payOrder, String taskType) {
        boolean allowedFlag = Boolean.FALSE;
        for (String allowedTaskType : VALIDTASKTYPES) {
            if (allowedTaskType.equals(taskType)) {
                allowedFlag = Boolean.TRUE;
                break;
            }
        }
        if (!allowedFlag) {
            LOG.warn("[return.createTask] invalid task type,check it. taskType:{},appid:{},apporderid:{}", taskType,
                    payOrder.getAppId(), payOrder.getAppOrderId());
            return;
        }
        Task specifiedTask = resource.getTask(payOrder.getAppId(), payOrder.getAppOrderId(), taskType);
        if (specifiedTask == null) {
            specifiedTask = new Task(payOrder.getAppId(), payOrder.getAppOrderId(), taskType, payOrder.getChId(),
                    payOrder.getPayMethod());
            resource.createTask(specifiedTask);
            LOG.info("[return.createTask] create task success,task:{}", specifiedTask);
        } else {
            LOG.info("[return.createTask] task exists,creating is unnessasary,task:{}", specifiedTask);
        }
    }
}
