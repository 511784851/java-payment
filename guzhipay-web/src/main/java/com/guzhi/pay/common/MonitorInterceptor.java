/*
 * Copyright (c) 2012 guzhi.com. 
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * guzhi. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 按分钟监控接口访问量，但并不会做访问拒绝。
 * 
 * 也可以改为按秒监控，但可能对性能影响较大，改为按分钟
 * 
 * @author administrator
 * 
 */
public class MonitorInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(MonitorInterceptor.class);
    private static final Logger HIIDO_LOG = LoggerFactory.getLogger("hiido_statistics");
    private static final int MAX_ELEMENT = 5000;
    private static final Map<MonitorInfo, Integer> monitors = Collections
            .synchronizedMap(new LinkedHashMap<MonitorInfo, Integer>(16, 0.75f, true) {
                private static final long serialVersionUID = 7124000420448653785L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<MonitorInfo, Integer> entry) {
                    return this.size() >= MAX_ELEMENT;
                }
            });
    private static final String DATE_TIME_FORMAT = "yyyyMMddHHmm";
    // 每个业务一分钟内最多可以访问一个接口1200次
    private static final Integer MAX_TIMES_PER_MINITUE = 1200;
    // 每分钟扫描一下监控信息
    private static final Long PERIOD = 1000 * 60L;

    // 防止遍历对象时发生ConcurrentModificationException
    private static final Semaphore signal = new Semaphore(1);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long time = System.nanoTime();
        String appId = request.getParameter("appId");
        if (StringUtils.isBlank(appId)) {
            // 不做统计
            return true;
        }
        String uri = request.getRequestURI();
        // yyyyMMddHHmm
        String dateTime = new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date());
        MonitorInfo info = new MonitorInfo(uri, appId, dateTime);
        while (!signal.tryAcquire())
            ;
        if (monitors.get(info) == null) {
            monitors.put(info, 1);
        } else {
            monitors.put(info, monitors.get(info) + 1);
        }
        signal.release();
        LOG.info("[monitorInterceptor] preHandle costTime:{}nano", System.nanoTime() - time);
        return true;
    }

    private static class MonitorInfo {
        String uri;
        String appId;
        String dateTime;
        int times;

        public MonitorInfo(String uri, String appId, String dateTime) {
            this.appId = appId;
            this.uri = uri;
            this.dateTime = dateTime;
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public String toString() {
            return "MonitorInfo [uri=" + uri + ", appId=" + appId + ", dateTime=" + dateTime + "]";
        }
    }

    // 启动监控线程
    {
        Calendar calendar = Calendar.getInstance();
        // 下一分钟开始执行
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
        calendar.set(Calendar.SECOND, 0);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
                long startTime = System.nanoTime();
                // 查看前一分钟的统计情况
                String preMinute = new SimpleDateFormat(DATE_TIME_FORMAT).format(DateUtils.addMinutes(new Date(), -1));
                // LOG.info("[monitorInterceptor] start preMinute:{}, size:{}",
                // preMinute, monitors.size());
                while (!signal.tryAcquire())
                    ;
                Map<MonitorInfo, Integer> map = new HashMap<MonitorInfo, Integer>(monitors);
                signal.release();

                Set<MonitorInfo> set = map.keySet();
                Iterator<MonitorInfo> it = set.iterator();
                List<MonitorInfo> infoList = new ArrayList<MonitorInfo>();
                while (it.hasNext()) {
                    try {
                        MonitorInfo info = it.next();
                        if (preMinute.equals(info.dateTime)) {
                            Integer times = map.get(info);
                            info.times = times;
                            infoList.add(info);
                            HIIDO_LOG.info("tpay;2;" + info.appId + ";" + info.uri + ";;" + info.dateTime + ";"
                                    + info.times + ";;;");
                            if (times != null && times > MAX_TIMES_PER_MINITUE) {
                                LOG.error(
                                        "[monitorInterceptor] ACCESS_OVERLOAD,  appId:{},uri:{},times:{} > {},dateTime:{}",
                                        info.appId, info.uri, times, MAX_TIMES_PER_MINITUE, info.dateTime);
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("[monitorInterceptor] schedule error ", e);
                    }
                }
                LOG.info("[monitorInterceptor] end costTime:{} nano", System.nanoTime() - startTime);
            }

        }, calendar.getTime(), PERIOD);
    }

    public static void main(String[] args) {
        new MonitorInterceptor();
    }
}
