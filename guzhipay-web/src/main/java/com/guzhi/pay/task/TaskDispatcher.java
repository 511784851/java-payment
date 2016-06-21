/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.guzhi.pay.business.PayService;
import com.guzhi.pay.channel.ChannelAdapterSelector;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.Task;

/**
 * @author administrator
 * 
 */
public class TaskDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(TaskDispatcher.class);
    // default thread pool, can be injected
    private TaskThreadPool defaultTaskThreadPool;

    // thread pool's mapping
    private Map<String, TaskThreadPool> poolMapping;

    private static final String PATTERN_SEPARATOR = ".";
    private static final String DEFAULT_POOL_NAME = "task.thread.pool.default";

    @Autowired
    private DomainResource resource;

    @Autowired
    private PayService payService;

    @Autowired
    @Qualifier("channelAdapterSelector")
    private ChannelAdapterSelector adapterSelector;

    // TODO Concern all exceptions
    public void doService(Task task) {
        TaskThreadPool ttp = getAppropriatePool(task);
        if (Task.TYPE_QUERY.equals(task.getType())) {
            ttp.execute(new QueryTaskRunable(payService, resource, task));
        } else if (Task.TYPE_REFUND.equals(task.getType())) { // 触发退款
            ttp.execute(new RefundTaskRunable(task, payService, resource));
        } else if (Task.TYPE_PAY_APPLE.equals(task.getType())) { // 苹果凭证验证
            ttp.execute(new AppleVerifyReceiptTaskRunnable(task, resource));
        } else if (Task.TYPE_NOTIFY.equals(task.getType())) {
            ttp.execute(new NotifyTaskRunable(resource, task));
        } else if (Task.TYPE_PAY_THYKT.equals(task.getType())) {// 天宏一卡通
            ttp.execute(new AsynPayTaskRunnable(task, resource));
        } else if (Task.TYPE_PAY_JWJK.equals(task.getType())) { // 骏网骏卡
            ttp.execute(new AsynPayTaskRunnable(task, resource));
        } else if (Task.TYPE_PAY_KQSZX.equals(task.getType())) {// 快钱神州行
            ttp.execute(new AsynPayTaskRunnable(task, resource));
        } else if (Task.TYPE_PAY_YEEPAYCARD.equals(task.getType())) {// 易宝神州行和易宝联通
            ttp.execute(new AsynPayTaskRunnable(task, resource));
        } else if (Task.TYPE_PAY_SZFCARD.equals(task.getType())) {// 神州付神州行和神州付联通
            ttp.execute(new AsynPayTaskRunnable(task, resource));
        } else if (Task.TYPE_PAY_ASYNC.equals(task.getType())) {// 异步充值
            ttp.execute(new AsynPayTaskRunnable(task, resource));
        }
    }

    private static final int OVERLOAD_MAX_SIZE = 10;

    /*
     * 获取合适的任务线程池
     */
    private TaskThreadPool getAppropriatePool(Task task) {
        String pattern = task.getType() + PATTERN_SEPARATOR + task.getChId() + PATTERN_SEPARATOR + task.getPayMethod();
        TaskThreadPool ttp = poolMapping.get(pattern.toLowerCase());
        if (ttp == null) {
            pattern = task.getChId() + PATTERN_SEPARATOR + task.getPayMethod();
            ttp = poolMapping.get(pattern.toLowerCase());
        }
        if (ttp == null) {
            pattern = task.getChId();
            ttp = poolMapping.get(pattern.toLowerCase());
        }
        if (ttp == null) {
            pattern = StringUtils.EMPTY;
            ttp = defaultTaskThreadPool;
        }
        int size = ttp.getQueue().size();
        if (size > OVERLOAD_MAX_SIZE) {
            LOG.info("[TaskThreadPool] OVERLOAD_ERROR, pool:{} , queue.size > {}, size:{}", ttp.getPoolName(),
                    OVERLOAD_MAX_SIZE, size);
        }
        return ttp;
    }

    public void setDefaultTaskThreadPool(TaskThreadPool defaultTaskThreadPool) {
        this.defaultTaskThreadPool = defaultTaskThreadPool;
    }

    public void setPoolMapping(Map<String, TaskThreadPool> poolMapping) {
        this.poolMapping = poolMapping;
    }

    public void init() {
        if (defaultTaskThreadPool == null) {
            defaultTaskThreadPool = new TaskThreadPool(20, DEFAULT_POOL_NAME);
        }
        if (poolMapping == null) {
            poolMapping = Collections.emptyMap();
        }
    }

}
