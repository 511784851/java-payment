package com.guzhi.pay.helper;

import static com.guzhi.pay.common.Consts.SC.DATA_FORMAT_ERROR;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;

import com.guzhi.pay.exception.PayException;

public class TimeHelper {

    // 以yyyMMddHHmmss 4位年+2位月+2位日+2位时+2位分+2位秒的格式返回当前时间。
    public static final String TIME_FORMATE = "yyyyMMddHHmmss";
    public static final String TIME_FORMATE1 = "yyyy-MM-dd HH:mm:ss";
    private static final int TIME_STR_LEN = 14;

    /**
     * 生成格式化的时间
     * 
     * @return
     */
    public static String getFormattedTime() {
        Format formatter = new SimpleDateFormat(TIME_FORMATE);
        return formatter.format(new Date());
    }

    /**
     * 验证时间格式是否正确。
     * 
     * @param time
     * @return
     */
    public static void validateTimeFormat(String time) {

        // 检查长度
        if (time == null || time.length() != TIME_STR_LEN) {
            throw new PayException(DATA_FORMAT_ERROR, "Time string error (length incorrect):" + time, null, null);
        }

        // 尝试解析
        try {
            Format formatter = new SimpleDateFormat(TIME_FORMATE);
            formatter.parseObject(time);
        } catch (ParseException e) {
            throw new PayException(DATA_FORMAT_ERROR, "Time string error (parse fail):" + time, null, null);
        }
    }

    public static StopWatch initTimer() {
        StopWatch watch = new StopWatch();
        watch.start();
        return watch;
    }

    public static long calcTime(StopWatch watch) {
        watch.stop();
        return watch.getTime();
    }

    public static long getTime(StopWatch watch) {
        return watch.getTime();
    }

    /**
     * 返回系统当前的完整日期时间 <br>
     * 格式 1：2008-05-02 13:12:44 <br>
     * 格式 2：2008/05/02 13:12:44 <br>
     * 格式 3：2008年5月2日 13:12:44 <br>
     * 格式 4：2008年5月2日 13时12分44秒 <br>
     * 格式 5：2008年5月2日 星期五 13:12:44 <br>
     * 格式 6：2008年5月2日 星期五 13时12分44秒 <br>
     * 
     * @param 参数(formatType) :格式代码号
     * @return 字符串
     */
    public static String get(int formatType, Date date) {
        SimpleDateFormat sdf = null;
        if (formatType == 1) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if (formatType == 2) {
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        } else if (formatType == 3) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        } else if (formatType == 4) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        } else if (formatType == 5) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 E HH:mm:ss");
        } else if (formatType == 6) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 E HH时mm分ss秒");
        } else if (formatType == 7) {
            sdf = new SimpleDateFormat("yyyyMMdd");
        } else if (formatType == 8) {
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        } else {
            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        }
        sdf.setLenient(false);
        return sdf.format(date);
    }

    /***
     * 当前日期加减n天
     * 
     * @param int
     * @param String
     * @return String
     */
    public static String nDayDate(int n, String format1) {
        Calendar cal = Calendar.getInstance();// 使用默认时区和语言环境获得一个日历。
        cal.add(Calendar.DAY_OF_MONTH, n);// 取当前日期的前一天.cal.add(Calendar.DAY_OF_MONTH,
                                          // +1);//取当前日期的后一天.
        // 通过格式化输出日期
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(format1);
        format.setLenient(false);

        return format.format(cal.getTime());
    }

    /**
     * 把字符串格式化为yyyy-MM-dd HH:mm:ss形式的时间Date
     * 
     * @param String
     * @return Date
     * @throws ParseException
     */
    public static Date strToDate(String dateString) {
        String lineOneString = dateString.substring(0, dateString.length());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(lineOneString.substring(0, 4));
        stringBuffer.append("-");
        stringBuffer.append(lineOneString.substring(4, 6));
        stringBuffer.append("-");
        stringBuffer.append(lineOneString.substring(6, 8));
        stringBuffer.append(" ");
        stringBuffer.append(dateString.substring(8, 10));
        stringBuffer.append(":");
        stringBuffer.append(dateString.substring(10, 12));
        stringBuffer.append(":");
        stringBuffer.append(dateString.substring(12, 14));
        return str2Date(stringBuffer.toString());
    }

    /**
     * 按照yyyy-MM-dd HH:mm:ss的格式，字符串转日期
     * 
     * @param String
     * @return Date
     */
    public static Date str2Date(String date) {
        if (!StringUtils.isBlank(date)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setLenient(false);
            try {
                return sdf.parse(date);
            } catch (ParseException e) {
            }
            return new Date();
        } else {
            return null;
        }
    }

    /**
     * 修改时
     * 
     * @param Date
     * @param int 正负决定增或减
     * @return Date
     */
    public static Date alterHour(Date date, int amount) {
        return alterDate(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 修改日期
     * 
     * @param Date (待修改日期)
     * @param int (修改的位置，分别表示年月日时分秒)
     * @param int (修改量大小，可正可负)
     * @return Date
     */
    private static Date alterDate(Date date, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();

    }

    /**
     * 将字符串转换成date
     * 
     * @param time
     * @param format
     * @return
     */
    public static Date strToDate(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 两个时间相减，获取毫秒差
     * 
     * @param start
     * @param end
     * @return
     */
    public static long dateSubDate(Date start, Date end) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        long endMill = cal.getTimeInMillis();
        cal.setTime(start);
        long startMill = cal.getTimeInMillis();
        return endMill - startMill;
    }

    /**
     * 返回：当前系统年份
     * 
     * @return String
     */
    public static String getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        return sdf.format(new java.util.Date()).split("-")[0];
    }

    public static String currentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(new Date());
    }

    public static String lastMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return sdf.format(calendar.getTime());
    }
}
