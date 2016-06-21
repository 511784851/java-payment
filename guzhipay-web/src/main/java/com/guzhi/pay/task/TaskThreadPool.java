/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.task;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author administrator
 * 
 */
public class TaskThreadPool extends ThreadPoolExecutor {
    private String poolName;

    /**
     * 创建线程数固定大小的线程池
     * 
     * @param poolSize
     * @param poolName 线程池的名称必须设置
     */
    public TaskThreadPool(int poolSize, String poolName) {
        super(poolSize, poolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory(
                poolName));
        this.poolName = poolName;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    private static class NamingThreadFactory implements ThreadFactory {
        private String threadName;
        private AtomicInteger counter = new AtomicInteger(1);

        public NamingThreadFactory(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public Thread newThread(Runnable r) {
        	//FIXME It seems the poolsize is not involed here. Is it right here?
            int index = counter.getAndIncrement();
            return new Thread(r, threadName + "-" + index);
        }
    }

    public String toString() {
        String str = super.toString();
        int idx = str.indexOf("[");
        if (idx == -1) {
            return "[name = " + poolName + "]";
        }
        String s = str.substring(idx + 1);
        return "[name = " + poolName + ", " + s;
    }
}
