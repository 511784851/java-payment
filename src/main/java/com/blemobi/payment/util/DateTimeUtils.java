package com.blemobi.payment.util;

import java.util.concurrent.TimeUnit;

public final class DateTimeUtils {
    private static final long DAY = 1 * 24 * 60 * 60 * 1000;
    private static final long HOURS = 1 * 60 * 60 * 1000;
    private static final long MINUTES = 1 * 60 * 1000;
    private static final long SECONDS = 1 * 1000;
    /**
     * @Description 时间计算
     * @author HUNTER.POON
     * @param tu 时间单位
     * @param num 数值（正+，负-）
     * @return
     */
    public static long calcTime(TimeUnit tu, long num){
        long ret = 0;
        long curr = System.currentTimeMillis();
        if(tu == TimeUnit.DAYS){
            ret = curr + (num * DAY);
        }else if(tu == TimeUnit.HOURS){
            ret = curr + (num * HOURS);
        }else if(tu == TimeUnit.MINUTES){
            ret = curr + (num * MINUTES);
        }else if(tu == TimeUnit.SECONDS){
            ret = curr + (num * SECONDS); 
        }else{
            throw new IllegalArgumentException("timeunit error");
        }
        return ret;
    }
    
    public static long currTime(){
        return System.currentTimeMillis();
    }
    
    public static boolean in24Hours(long ms){
        long sub = currTime() - ms;
        return DAY > sub;
    }
    
    public static void main(String[] args) {
        System.out.println(DateTimeUtils.calcTime(TimeUnit.DAYS, -30));
    }
}
