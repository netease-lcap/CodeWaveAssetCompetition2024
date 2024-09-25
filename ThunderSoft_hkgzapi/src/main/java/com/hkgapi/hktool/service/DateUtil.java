//package com.hkgapi.hktool.service;
//
//import com.netease.lowcode.core.annotation.NaslLogic;
//import com.netease.lowcode.core.annotation.NaslStructure;
//import org.apache.commons.lang3.StringUtils;
//import org.joda.time.DateTime;
//import org.springframework.format.datetime.DateFormatter;
//import org.springframework.stereotype.Component;
//import sun.util.calendar.BaseCalendar;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
///**
// * 日期工具类
// */
//@Component
//public class DateUtil {
//    /**
//     * 全局默认日期格式
//     */
//    public static final String Format_Date = "yyyy-MM-dd";
//
//
//    /**
//     * 全局默认日期格式
//     */
//    public static final String Format_Date_Dir = "yyyy/MM/dd";
//
//    /**
//     * 全局默认时间格式
//     */
//    public static final String Format_Time = "HH:mm:ss";
//
//    /**
//     * 全局默认日期时间格式
//     */
//    public static final String Format_DateTime = "yyyy-MM-dd HH:mm:ss";
//
//    /**
//     * 全局默认日期时间格式
//     */
//    public static final String Format_DateTime1 = "yyyyMMddhhmmss";
//
//    /**
//     * 得到以yyyy-MM-dd格式表示的当前日期字符串
//     */
//    @NaslLogic(enhance = false)
//    public static String getCurrentDate() {
//        return new SimpleDateFormat(Format_Date).format(new Date());
//    }
//    /**
//     * 得到以yyyy/MM/dd格式表示的当前日期字符串dir
//     */
//    @NaslLogic(enhance = false)
//    public static String getCurrentDateDir() {
//        return new SimpleDateFormat(Format_Date_Dir).format(new Date());
//    }
//
//
//
//
//
//    /**
//     * 得到以HH:mm:ss表示的当前时间字符串
//     */
//    @NaslLogic(enhance = false)
//    public   String getCurrentTime() {
//        return new SimpleDateFormat(Format_Time).format(new Date());
//    }
//
//
//
//
//
//
//
//    /**
//     * 今天是星期几
//     *
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Integer getDayOfWeek() {
//        Calendar cal = Calendar.getInstance();
//        return cal.get(Calendar.DAY_OF_WEEK);
//    }
//
//
//    /**
//     * 今天是本月的第几天
//     *
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Integer getDayOfMonth() {
//        Calendar cal = Calendar.getInstance();
//        return cal.get(Calendar.DAY_OF_MONTH);
//    }
//
////    /**
////     * 指定日期是当月的第几天
////     *
////     * @param dateT
////     * @return
////     */
////    @NaslLogic(enhance = false)
////    public   Integer getDayOfMonth(String dateT) {
////        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
////        Date datet = null;
////        try {
////            datet = dateFormat.parse(dateT);
////        } catch (ParseException e) {
////            e.printStackTrace();
////        }
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(datet);
////        return cal.get(Calendar.DAY_OF_MONTH);
////    }
//
////    /**
////     * 获取某一个月的天数
////     *
////     * @param date
////     * @return
////     */
////    @NaslLogic(enhance = false)
////    public   Integer getMaxDayOfMonth(Date date) {
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(date);
////        return cal.getActualMaximum(Calendar.DATE);
////    }
////
////    /**
////     * 以yyyy-MM-dd格式获取某月的第一天
////     *
////     * @param date
////     * @return
////     */
////    @NaslLogic(enhance = false)
////    public   String getFirstDayOfMonthT(String date) {
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(parse(date));
////        cal.set(Calendar.DAY_OF_MONTH, 1);
////        return new SimpleDateFormat(Format_Date).format(cal.getTime());
////    }
//
//    /**
//     * 今天是本年的第几天
//     *
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Integer getDayOfYear() {
//        Calendar cal = Calendar.getInstance();
//        return cal.get(Calendar.DAY_OF_YEAR);
//    }
//
////    /**
////     * 指定日期是当年的第几天
////     *
////     * @param date
////     * @return
////     */
////    @NaslLogic(enhance = false)
////    public   Integer getDayOfYear(Date date) {
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(date);
////        return cal.get(Calendar.DAY_OF_YEAR);
////    }
//
////    /**
////     * 以yyyy-MM-dd解析字符串date，并返回其表示的日期是周几
////     *
////     * @param date
////     * @return
////     */
////    @NaslLogic(enhance = false)
////    public   Integer getDayOfWeek(String date) {
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(parse(date));
////        return cal.get(Calendar.DAY_OF_WEEK);
////    }
////
//
//
////    /**
////     * 以yyyy-MM-dd解析字符串date，并返回其表示的日期是当年第几天
////     *
////     * @param date
////     * @return
////     */
////    @NaslLogic(enhance = false)
////    public   Integer getDayOfYear(String date) {
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(parse(date));
////        return cal.get(Calendar.DAY_OF_YEAR);
////    }
//
//    /**
//     * 以指定的格式返回当前日期时间的字符串
//     *
//     * @param format
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   String getCurrentDateTime(String format) {
//        SimpleDateFormat t = new SimpleDateFormat(format);
//        return t.format(new Date());
//    }
//
////    /**
////     * 以yyyy-MM-dd格式输出只带日期的字符串
////     */
////    @NaslLogic(enhance = false)
////    public   String toString(Date date) {
////        if (date == null) {
////            return "";
////        }
////        return new SimpleDateFormat(Format_Date).format(date);
////    }
//
////    /**
////     * 以yyyy-MM-dd HH:mm:ss输出带有日期和时间的字符串
////     */
////    @NaslLogic(enhance = false)
////    public   String toDateTimeString(Date date) {
////        if (date == null) {
////            return "";
////        }
////        return new SimpleDateFormat(Format_DateTime).format(date);
////    }
//
////    /**
////     * 以yyyy-MM-dd HH:mm:ss输出带有日期和时间的字符串
////     */
////    @NaslLogic(enhance = false)
////    public   String toDateTimeString(DateTime date) {
////        if (date == null) {
////            return "";
////        }
////        return	org.joda.time.format.DateTimeFormat.forPattern(Format_DateTime).print(date);
////
////    }
//
////    /**
////     * 按指定的format输出日期字符串
////     */
////    @NaslLogic(enhance = false)
////    public   String toString(Date date, String format) {
////        SimpleDateFormat t = new SimpleDateFormat(format);
////        return t.format(date);
////    }
////
////    /**
////     * 以HH:mm:ss输出只带时间的字符串
////     */
////    @NaslLogic(enhance = false)
////    public   String toTimeString(Date date) {
////        if (date == null) {
////            return "";
////        }
////        return new SimpleDateFormat(Format_Time).format(date);
////    }
////
//    /**
//     * 以yyyy-MM-dd解析两个字符串，并比较得到的两个日期的大小
//     *
//     * @param date1
//     * @param date2
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Integer compare(String date1, String date2) {
//        return compare(date1, date2, Format_Date);
//    }
//
//    /**
//     * 以HH:mm:ss解析两个字符串，并比较得到的两个时间的大小
//     *
//     * @param time1
//     * @param time2
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Integer compareTime(String time1, String time2) {
//        return compareTime(time1, time2, Format_Time);
//    }
//
////    /**
////     * 以指定格式解析两个字符串，并比较得到的两个日期的大小
////     *
////     * @param date1
////     * @param date2
////     * @param format
////     * @return
////     */
////    @NaslLogic(enhance = false)
////    public   Integer compare(String date1, String date2, String format) {
////        Date d1 = parse(date1, format);
////        Date d2 = parse(date2, format);
////        return d1.compareTo(d2);
////    }
//
//    /**
//     * 以指定解析两个字符串，并比较得到的两个时间的大小
//     *
//     * @param time1
//     * @param time2
//     * @param format
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Integer compareTime(String time1, String time2, String format) {
//        String[] arr1 = time1.split(":");
//        String[] arr2 = time2.split(":");
//        if (arr1.length < 2) {
//            throw new RuntimeException("错误的时间值:" + time1);
//        }
//        if (arr2.length < 2) {
//            throw new RuntimeException("错误的时间值:" + time2);
//        }
//        int h1 = Integer.parseInt(arr1[0]);
//        int m1 = Integer.parseInt(arr1[1]);
//        int h2 = Integer.parseInt(arr2[0]);
//        int m2 = Integer.parseInt(arr2[1]);
//        int s1 = 0, s2 = 0;
//        if (arr1.length == 3) {
//            s1 = Integer.parseInt(arr1[2]);
//        }
//        if (arr2.length == 3) {
//            s2 = Integer.parseInt(arr2[2]);
//        }
//        if (h1 < 0 || h1 > 23 || m1 < 0 || m1 > 59 || s1 < 0 || s1 > 59) {
//            throw new RuntimeException("错误的时间值:" + time1);
//        }
//        if (h2 < 0 || h2 > 23 || m2 < 0 || m2 > 59 || s2 < 0 || s2 > 59) {
//            throw new RuntimeException("错误的时间值:" + time2);
//        }
//        if (h1 != h2) {
//            return h1 > h2 ? 1 : -1;
//        } else {
//            if (m1 == m2) {
//                if (s1 == s2) {
//                    return 0;
//                } else {
//                    return s1 > s2 ? 1 : -1;
//                }
//            } else {
//                return m1 > m2 ? 1 : -1;
//            }
//        }
//    }
//
//    /**
//     * 判断指定的字符串是否符合HH:mm:ss格式，并判断其数值是否在正常范围
//     *
//     * @param time
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Boolean isTime(String time) {
//        String[] arr = time.split(":");
//        if (arr.length < 2) {
//            return false;
//        }
//        try {
//            int h = Integer.parseInt(arr[0]);
//            int m = Integer.parseInt(arr[1]);
//            int s = 0;
//            if (arr.length == 3) {
//                s = Integer.parseInt(arr[2]);
//            }
//            if (h < 0 || h > 23 || m < 0 || m > 59 || s < 0 || s > 59) {
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 判断指定的字符串是否符合yyyy:MM:ss格式，但判断其数据值范围是否正常
//     *
//     * @param date
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public Boolean isDate(String date) {
//        String[] arr = date.split("-");
//        if (arr.length < 3) {
//            return false;
//        }
//        try {
//            int y = Integer.parseInt(arr[0]);
//            int m = Integer.parseInt(arr[1]);
//            int d = Integer.parseInt(arr[2]);
//            if (y < 0 || m > 12 || m < 0 || d < 0 || d > 31) {
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 判断指定日期是否是周末
//     *
//     * @param date
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Boolean isWeekend(Date date) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        int t = cal.get(Calendar.DAY_OF_WEEK);
//        if (t == Calendar.SATURDAY || t == Calendar.SUNDAY) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 以yyyy-MM-dd解析指定字符串，并判断相应的日期是否是周末
//     *
//     * @param str
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Boolean isWeekend(String str) {
//        return isWeekend(parse(str));
//    }
//
//
//
//    /**
//     * 以yyyy-MM-dd解析指定字符串，返回相应java.util.Date对象
//     *
//     * @param str
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Date parse(String str) {
//        if (StringUtils.isEmpty(str)) {
//            return null;
//        }
//        try {
//            return new SimpleDateFormat(Format_Date).parse(str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 按指定格式解析字符串，并返回相应的java.util.Date对象
//     *
//     * @param str
//     * @param format
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Date parse(String str, String format) {
//        if (StringUtils.isEmpty(str)) {
//            return null;
//        }
//        try {
//            SimpleDateFormat t = new SimpleDateFormat(format);
//            return t.parse(str);
//        } catch (ParseException e) {
//            //	e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 以yyyy-MM-dd HH:mm:ss格式解析字符串，并返回相应的java.util.Date对象
//     *
//     * @param str
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Date parseDateTime(String str) {
//        if (StringUtils.isEmpty(str)) {
//            return null;
//        }
//        if (str.length() <= 10) {
//            return parse(str);
//        }
//        try {
//            return new SimpleDateFormat(Format_DateTime).parse(str);
//        } catch (ParseException e) {
//            //	e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 以指定格式解析字符串，并返回相应的java.util.Date对象
//     *
//     * @param str
//     * @param format
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Date parseDateTime(String str, String format) {
//        if (StringUtils.isEmpty(str)) {
//            return null;
//        }
//        try {
//            SimpleDateFormat t = new SimpleDateFormat(format);
//            return t.parse(str);
//        } catch (ParseException e) {
//            //	e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * 日期date上加count分钟，count为负表示减
//     */
//    @NaslLogic(enhance = false)
//    public   Date addMinute(Date date, Integer count) {
//        return new Date(date.getTime() + 60000L * count);
//    }
//
//    /**
//     * 日期date上加count小时，count为负表示减
//     */
//    @NaslLogic(enhance = false)
//    public   Date addHour(Date date, Integer count) {
//        return new Date(date.getTime() + 3600000L * count);
//    }
//
//    /**
//     * 日期date上加count天，count为负表示减
//     */
//    @NaslLogic(enhance = false)
//    public   Date addDay(Date date, Integer count) {
//        return new Date(date.getTime() + 86400000L * count);
//    }
//
//    /**
//     * 日期date上加count星期，count为负表示减
//     */
//    @NaslLogic(enhance = false)
//    public   Date addWeek(Date date, Integer count) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.add(Calendar.WEEK_OF_YEAR, count);
//        return c.getTime();
//    }
//
//    /**
//     * 日期date上加count月，count为负表示减
//     */
//    @NaslLogic(enhance = false)
//    public   Date addMonth(Date date, Integer count) {
//		/* ${_ZVING_LICENSE_CODE_} */
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.add(Calendar.MONTH, count);
//        return c.getTime();
//    }
//
//    /**
//     * 日期date上加count年，count为负表示减
//     */
//    @NaslLogic(enhance = false)
//    public   Date addYear(Date date, Integer count) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.add(Calendar.YEAR, count);
//        return c.getTime();
//    }
//
//    /**
//     * 人性化显示时间日期,date格式为：yyyy-MM-dd HH:mm:ss
//     *
//     * @param date
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   String toDisplayDateTime(String date) {
//        if (StringUtils.isEmpty(date)) {
//            return null;
//        }
//        try {
//            if (isDate(date)) {
//                return toDisplayDateTime(parse(date));
//            } else {
//                SimpleDateFormat t = new SimpleDateFormat(Format_DateTime);
//                Date d = t.parse(date);
//                return toDisplayDateTime(d);
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "不是标准格式时间!";
//        }
//    }
//
//    /**
//     * 人性化显示时间日期
//     *
//     * @param date
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   String toDisplayDateTime(Date date) {
//        long minite = (System.currentTimeMillis() - date.getTime()) / 60000L;
//        if (minite < 60) {
//            return toString(date, "yyyy-MM-dd") + " " + minite + "分钟前";
//        }
//        if (minite < 60 * 24) {
//            return toString(date, "yyyy-MM-dd") + " " + minite / 60L + "小时前";
//        } else {
//            return toString(date, "yyyy-MM-dd") + " " + minite / 1440L + "天前";
//        }
//    }
//
//
//    /**
//     * 传入秒数 返回 xx:xx:xx这种时间格式
//     * @param second
//     * @return
//     */
//
//    @NaslLogic(enhance = false)
//    public   String getTimeStr(long second) {
//
//        String timsStr = "";
//        String hourStr = String.valueOf(second / (60 * 60));
//        long second1 = second % 3600;
//        if (hourStr.length() == 1) {
//            hourStr = "0" + hourStr;
//        }
//        String minite = String.valueOf(second1 / 60);
//        if (minite.length() == 1) {
//            minite = "0" + minite;
//        }
//        String second2 = String.valueOf(second1 % 60);
//        if (second2.length() == 1) {
//            second2 = "0" + second2;
//        }
//        timsStr = hourStr + ":" +minite + ":" + second2;
//        return timsStr;
//    }
//    @NaslLogic(enhance = false)
//    public   String getFormatTimeStr(String timeStr){
//        String str = "";
//        if(timeStr.length() != 0) {
//            str = timeStr.replaceAll(":","'");
//        }
//        return str;
//    }
//
//    @NaslLogic(enhance = false)
//    public   String getTotalSecond(String dateStr){
//        String result = "";
//        if(StringUtils.isNotEmpty(dateStr)&&dateStr.indexOf(":")>-1){
//            String[] arr = dateStr.split(":");
//            Long hour = Long.valueOf(arr[0])*60*60;
//            Long min = Long.valueOf(arr[1])*60;
//            Long second = Long.valueOf(arr[2]);
//            result = String.valueOf(hour+min+second);
//        }else{
//            result = "0";
//        }
//        return result;
//
//    }
//    /**
//     * 计算2个时间戳日期的天数差 date2 -date1
//     * @param date1
//     * @param date2
//     * @return
//     * @throws ParseException
//     */
//    @NaslLogic(enhance = false)
//    public   Integer getDaysDifference(Long date1, Long date2) throws ParseException{
//        Date d1 = new Date(date1);
//        Date d2 = new Date(date2);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        d1 = sdf.parse(sdf.format(d1));
//        d2 = sdf.parse(sdf.format(d2));
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(d1);
//        Long time1 = cal.getTimeInMillis();
//        cal.setTime(d2);
//        Long time2 = cal.getTimeInMillis();
//        long between_days=(time2-time1)/(1000*3600*24);
//        return Integer.parseInt(String.valueOf(between_days));
//    }
//
//
//
//    /**
//     * 得到一天的开始时间
//     * @param date
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Date getStartTimeOfDay(Date date){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        Date start = calendar.getTime();
//        return start;
//    }
//
//    /**
//     * 得到一天的结束时间
//     * @param date
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   Date getEndTimeOfDay(Date date){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 59);
//        calendar.set(Calendar.SECOND, 59);
//        calendar.set(Calendar.MILLISECOND, 999);
//        Date end = calendar.getTime();
//        return end;
//    }
//
//    /**
//     * 判断当前时间距离第二天凌晨的秒数
//     *
//     * @return 返回值单位为[s:秒]
//     */
//    @NaslLogic(enhance = false)
//    public   Long getSecondsNextEarlyMorning() {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_YEAR, 1);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
//    }
//
//
//
//    /**
//     * 得到一天的结束时间
//     * @param date
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public    Date getEndTime(Date date){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.add(Calendar.DAY_OF_MONTH, 1);
//        calendar.add(Calendar.SECOND, -1);
//        Date end = calendar.getTime();
//        return end;
//    }
//
//
//    /**
//     * 获取过去第几天的日期
//     *
//     * @param past
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public  String getPastDate(Integer past, Date date) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - past);
//        Date day = calendar.getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat(Format_Date);
//        String result = sdf.format(day);
//        return result;
//    }
//
//    private    String getYmdStrByNow(Date date) {
//        if (date == null) {
//            return null;
//        }
//        SimpleDateFormat sdf = new SimpleDateFormat(Format_Date);
//        String result = sdf.format(date);
//        return result;
//    }
//
//    /**
//     * 获取某段时间内的所有日期
//     * @param startDate
//     * @param endDate
//     * @return
//     */
//    @NaslLogic(enhance = false)
//    public   List<String> findDates(String startDate, String endDate) {
//        Date dStart = parse(startDate);
//        Date dEnd = parse(endDate);
//        Calendar cStart = Calendar.getInstance();
//        cStart.setTime(dStart);
//        List dateList = new ArrayList();
//        startDate = getYmdStrByNow(dStart);
//        dateList.add(startDate);
//        while (dEnd.after(cStart.getTime())) {
//            cStart.add(Calendar.DAY_OF_MONTH, 1);
//            Date current = cStart.getTime();
//            dateList.add(toString(current));
//        }
//        return dateList;
//    }
//
//
//    /**
//     * 获取指定日期前一天日期
//     */
//    @NaslLogic(enhance = false)
//    public   String getBefore(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat(Format_Date);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        calendar.add(Calendar.DATE, -1);
//        String before = sdf.format(calendar.getTime());
//        return before;
//    }
//
//}