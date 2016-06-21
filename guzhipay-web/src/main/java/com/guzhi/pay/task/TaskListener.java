/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.Task;
import com.guzhi.pay.util.NetUtils;

/**
 * @author administrator
 */
public class TaskListener {
    @Autowired
    private TaskDispatcher dispatcher;
    @Autowired
    private DomainResource resource;

    private static Logger logger = LoggerFactory.getLogger(TaskListener.class);
    private static final int FIXED_THREAD_SIZE = 1;
    private TaskThreadPool pool = new TaskThreadPool(FIXED_THREAD_SIZE, "task.listener");
    // 上海机房不能调用联动的查询接口
    // 222.73.61.39,140.207.208.39
    // 222.73.64.70,140.207.208.134
    // 222.73.64.71,140.207.208.135
    private static String BLACK_IP = "222.73.61.39,140.207.208.39,222.73.64.70,140.207.208.134,222.73.64.71,140.207.208.135";
    private boolean flag;
    private static boolean queryFlag = false;
    static {
        String ip = NetUtils.getIP();
        logger.info("the ip address :{}", ip);
        if (StringUtils.contains(BLACK_IP, ip)) {
            queryFlag = true;
        }
    }

    public void init() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < FIXED_THREAD_SIZE; i++) {
                    logger.debug("start threads for task dispatch, count: {}", i);
                    pool.execute(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    List<Task> tasks = null;
                                    if (flag) {
                                        tasks = resource.getExcutableTaskByType(Task.TYPE_QUERY,
                                                Consts.Task.RETRY_MAX_TIMES, 20);
                                    } else {
                                        tasks = resource.getExcutableTaskUnType(Task.TYPE_QUERY,
                                                Consts.Task.RETRY_MAX_TIMES, 20);
                                    }
                                    // 没有可执行的任务,休眠200ms
                                    if (CollectionUtils.isEmpty(tasks)) {
                                        Thread.sleep(200);
                                    } else {
                                        CountDownLatch cdl = null;
                                        if (flag) {
                                            cdl = new CountDownLatch(tasks.size());
                                        }
                                        for (Task task : tasks) {
                                            task.setCdl(cdl);
                                            String appId = task.getAppId();
                                            String appOrderId = task.getAppOrderId();
                                            // 移动短信的的查询任务不能做
                                            if (Task.TYPE_QUERY.equalsIgnoreCase(task.getType())
                                                    && Consts.Channel.SMS.equalsIgnoreCase(task.getChId()) && queryFlag) {
                                                if (cdl != null) {
                                                    cdl.countDown();
                                                }
                                                continue;
                                            }
                                            int updateResult = resource.updateTaskToOccupied(appId, appOrderId,
                                                    task.getType(), task.getRetryTimes());
                                            logger.info(
                                                    "appi:{},apporderid:{},type:{},update task result:{},retryTimes:{}",
                                                    appId, appOrderId, task.getType(), updateResult,
                                                    task.getRetryTimes());
                                            if (updateResult != 1) {
                                                logger.error(
                                                        "task occuppied by other thread, task stopped! updateResult={}",
                                                        updateResult);
                                                if (cdl != null) {
                                                    cdl.countDown();
                                                }
                                                continue;
                                            }
                                            logger.debug("found task for dispatch, task: {}", task);
                                            dispatcher.doService(task);
                                        }
                                        if (cdl != null) {
                                            boolean awaitResult = cdl.await(60, TimeUnit.SECONDS);
                                            if (!awaitResult) {
                                                logger.error("countdownLatch timeout");
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.warn("exception in task dispatch thread!", e);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ie) {
                                        logger.warn("exception when making dispatch thread sleep!", ie);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }, 1000);

    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

}
