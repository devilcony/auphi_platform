package com.aofei.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Hao
 * @create 2017-04-10
 */
public class DateUtils {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String MM_DD = "MM-dd";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String HH_MM = "HH:mm";
    public static final String YYYYMMDD = "yyyyMMdd";

    public final static DateTimeFormatter FORMATTER_YYYY_MM_DD_HH_MM_SS = DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM_SS);
    public final static DateTimeFormatter FORMATTER_YYYY_MM_DD_HH_MM = DateTimeFormat.forPattern(YYYY_MM_DD_HH_MM);
    public final static DateTimeFormatter FORMATTER_YYYY_MM_DD = DateTimeFormat.forPattern(YYYY_MM_DD);
    public final static DateTimeFormatter FORMATTER_MM_DD = DateTimeFormat.forPattern(MM_DD);
    public final static DateTimeFormatter FORMATTER_HH_MM_SS = DateTimeFormat.forPattern(HH_MM_SS);
    public final static DateTimeFormatter FORMATTER_HH_MM = DateTimeFormat.forPattern(HH_MM);
    public final static DateTimeFormatter FORMATTER_YYYYMMDD = DateTimeFormat.forPattern(YYYYMMDD);

    public static String format(String dateTime, String targetPattern) {
        return format(dateTime, FORMATTER_YYYY_MM_DD_HH_MM_SS, targetPattern);
    }

    public static String format(String dateTime, DateTimeFormatter formatter, String targetPattern) {
        DateTime result = DateTime.parse(dateTime, formatter);
        return result.toString(targetPattern);
    }

    public static String format(Date time, String targetPattern) {
        DateTime dateTime = new DateTime(time);
        return dateTime.toString(targetPattern,Locale.ENGLISH);
    }

    public static Date format(String dateTime, DateTimeFormatter formatter) {
        DateTime time = DateTime.parse(dateTime, formatter);
        return time.toDate();
    }

    public static Date format(String dateTime) {
        return format(dateTime, FORMATTER_YYYY_MM_DD_HH_MM_SS);
    }

    public static String toYmd(Date dateTime) {
        return format(dateTime, YYYY_MM_DD);
    }

    public static String toMd(Date dateTime) {
        return format(dateTime, MM_DD);
    }

    public static String toHm(Date dateTime) {
        return format(dateTime, HH_MM);
    }

    public static String toYmdHm(Date dateTime) {
        return format(dateTime, YYYY_MM_DD_HH_MM);
    }

    public static Date startOfDay(Date value) {
        DateTime dateTime = new DateTime(value);
        dateTime = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), 00, 00, 00);
        return dateTime.toDate();
    }

    public static Date endOfDay(Date value) {
        DateTime dateTime = new DateTime(value);
        dateTime = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), 23, 59, 59);
        return dateTime.toDate();
    }

    public static int  toInt(Date dateTime) {
        String dateStr = format(dateTime, YYYYMMDD);
        return Integer.valueOf(dateStr);
    }



    /**
     * 获取两个日期之间的天数
     * @param date1
     * @param date2
     * @param isExactMillisecond 是否精确到毫秒 比如2017年6月1号 21:21:28,2017年6月2号 20:21:28,true:相差天数为0，false相差天数为1
     * @author yutao
     * @return date2大于date1的话,返回正数,否则返回负数.
     * @date 2017年6月19日下午6:00:21
     */
    public static int getDaysDifference(Date date1, Date date2, boolean isExactMillisecond){
        /*Date temp;//如果不区分date2和date1谁大谁小,可以解开注释
        if(date1.getTime() > date2.getTime()){
            temp = date2;
            date2=date1;
            date1=temp;
        }*/
        int days = 1;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        int year1 = cal.get(Calendar.YEAR);
        int dayOfYear1 = cal.get(Calendar.DAY_OF_YEAR);

        cal.clear();

        cal.setTime(date2);
        int year2 = cal.get(Calendar.YEAR);
        int dayOfYear2 = cal.get(Calendar.DAY_OF_YEAR);
        long diffTime = date2.getTime() - date1.getTime();

        if(isExactMillisecond){//精准考虑
            return (int) (diffTime / (1000*3600*24));
        }
        //不考虑时间，只考虑日期
        if(year1==year2 && dayOfYear1 == dayOfYear2){//同一天
            days = 0;
        }else{
            days = (int) (diffTime / (1000*3600*24));
            if(diffTime >= (1000*3600*24)){//不同天,且大于一天
                if(diffTime % (1000*3600*24) > 0){
                    //假设为1.5天,就算2天
                    days++;
                }
            }
        }
        return days;
    }

    /**
     * 获取前后日期 i为正数 向后推迟i天，负数时向前提前i天
     * @param i
     * @return
     */
    public static Date getdate(Date date,int i){
        Date dat = null;
        Calendar cd = Calendar.getInstance();
        cd.setTime(date);
        cd.add(Calendar.DATE, i);
        dat = cd.getTime();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = Timestamp.valueOf(dformat.format(dat));
        return timestamp;
    }

}
