package com.blemobi.payment.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;
@Log4j
public final class DateTimeUtils {
    private static final long DAY = 1 * 24 * 60 * 60 * 1000;
    private static final long HOURS = 1 * 60 * 60 * 1000;
    private static final long MINUTES = 1 * 60 * 1000;
    private static final long SECONDS = 1 * 1000;
    
    public static String getDateTime14() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
    public static String getDate8() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }
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
    
    public static boolean in24Hours(long ms1, long ms2){
        long sub = ms1 - ms2;
        return DAY > sub;
    }
    public static boolean in24Hours1(long ot, long ms2){
        if(ot <= ms2){
            return false;
        }
        long sub = (ms2 + DAY) - ot;
        return 0 <= sub && sub <= DAY;
    }
    
    public static int compare(long dt1, long dt2){
        if(dt1 > dt2)
            return 1;
        if(dt1 < dt2)
            return -1;
        return 0;
    }
    
    public static void main(String[] args) {
        System.out.println(DateTimeUtils.calcTime(TimeUnit.DAYS, -30));
    }
}
