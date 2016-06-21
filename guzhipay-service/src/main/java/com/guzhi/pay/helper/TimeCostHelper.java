/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;

/**
 * 耗时统计
 * 
 * @author administrator
 * 
 */
public class TimeCostHelper {
    private final static ThreadLocal<TimeCostInfo> tl = new ThreadLocal<TimeCostInfo>();

    private static class TimeCostInfo {
        StopWatch watch;
        Map<String, Object> carrier;

        public TimeCostInfo(Map<String, Object> carrier, StopWatch watch) {
            this.watch = watch;
            this.carrier = carrier;
        }

    }

    public static void watch(Map<String, Object> carrier) {
        StopWatch watch = new StopWatch();
        watch.start();
        tl.set(new TimeCostInfo(carrier, watch));
    }

    public static void suspend() {
        if (tl.get() != null) {
            tl.get().watch.suspend();
        }
    }

    public static void resume() {
        if (tl.get() != null) {
            tl.get().watch.resume();
        }
    }

    /**
     * 返回耗时
     * 
     * @return
     */
    public static long getTimeCost() {
        if (tl.get() != null) {
            return tl.get().watch.getTime();
        }
        return 0L;
    }

    /**
     * 返回需要携带的属性
     * 
     * @return
     */
    public static Map<String, Object> getCarrier() {
        if (tl.get() != null) {
            return tl.get().carrier;
        }
        return null;
    }

    public static void clean() {
        tl.remove();
    }

    public static void main(String[] args) throws InterruptedException {
        TimeCostHelper.watch(null);
        Thread.sleep(200);
        TimeCostHelper.suspend();
        Thread.sleep(3000);

        System.out.println(TimeCostHelper.getTimeCost());

    }
}
