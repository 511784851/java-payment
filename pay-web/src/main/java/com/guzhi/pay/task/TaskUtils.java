/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.Task;

/**
 * @author administrator
 * 
 */
public class TaskUtils {

    private static final Logger logger = LoggerFactory.getLogger(TaskUtils.class);

    public static void refresh(DomainResource resource, Task task, int interval) {
        // 10次，Interval为10秒时，各次间隔：1：10秒，2：1分钟，3：4分钟，4：10分钟，5：20分钟，6：36分钟，7：57分钟，8：85分钟，9：2小时，10：2小时，总共时长：8小时
        int delayLen = (int) Math.pow(task.getRetryTimes(), 3) * interval;
        Date nextTime = DateUtils.addSeconds(new Date(), delayLen);
        refreshNextTime(resource, task, nextTime);
    }

    private static void refreshNextTime(DomainResource resource, Task task, Date nextTime) {
        if (task.getRetryTimes() >= Consts.Task.RETRY_MAX_TIMES) {
            logger.warn("[TaskUtils]task failed, task={}", task);
            return;// 暂时不删除这样情况的任务，方便观察这种情况发生的概率
        }
        resource.refreshTaskWithDelay(task.getAppId(), task.getAppOrderId(), task.getType(), nextTime);
    }

    /**
     * 下次定时任务执行是固定的时间间隔
     * 
     * @param resource
     * @param task
     * @param interval
     */
    public static void refreshEqualInterval(DomainResource resource, Task task, int interval) {
        Date nextTime = DateUtils.addSeconds(new Date(), interval);
        refreshNextTime(resource, task, nextTime);
    }

}
