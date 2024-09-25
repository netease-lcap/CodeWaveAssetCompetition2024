package com.hq.unitApi.service;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * 此工具类包含了基本上常用的工具类，包含对于各种类型的日期的转换，以及转成我们所需要的各种类型日期格式。
 */
@Component
public class DateUtil {

    /**
     * 仅显示年月日，例如 2015-08-11.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 显示年月日时分秒，例如 2015-08-11 09:51:53.
     */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 仅显示时分秒，例如 09:51:53.
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 仅显示年月，例如 2015-08
     */
    public static final String MONTH_TIME_PATTERN = "yyyy-MM";

    /**
     * 仅显示年，例如 2015
     */
    public static final String YEAR_TIME_PATTERN = "yyyy";

    /**
     * 每天的毫秒数 8640000.
     */
    public static final long MILLISECONDS_PER_DAY = 86400000L;

    /**
     * 每周的天数.
     */
    public static final long DAYS_PER_WEEK = 7L;

    /**
     * 每小时毫秒数.
     */
    public static final long MILLISECONDS_PER_HOUR = 3600000L;

    /**
     * 每分钟秒数.
     */
    public static final long SECONDS_PER_MINUTE = 60L;

    /**
     * 每小时秒数.
     */
    public static final long SECONDS_PER_HOUR = 3600L;

    /**
     * 每天秒数.
     */
    public static final long SECONDS_PER_DAY = 86400L;

    /**
     * 每个月秒数，默认每月30天.
     */
    public static final long SECONDS_PER_MONTH = 2592000L;

    /**
     * 每年秒数，默认每年365天.
     */
    public static final long SECONDS_PER_YEAR = 31536000L;

    /**
     * 常用的时间格式.
     */
    private static String[] parsePatterns = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm"};

    /**
     * 得到当前日期字符串.
     *
     * @return String 日期字符串，例如2015-08-11
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getDatetoString() {
        return getDate(DateUtil.DATE_FORMAT);
    }

    /**
     * 得到当前时间字符串.
     *
     * @return String 时间字符串，例如 09:51:53
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getTime() {
        return formatDate(new Date(), DateUtil.TIME_FORMAT);
    }

    /**
     * 得到当前日期和时间字符串.
     *
     * @return String 日期和时间字符串，例如 2015-08-11 09:51:53
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getDateTime() {
        return formatDate(new Date(), DateUtil.DATETIME_FORMAT);
    }

    /**
     * 获取当前时间指定格式下的字符串.
     *
     * @param pattern 转化后时间展示的格式，例如"yyyy-MM-dd"，"yyyy-MM-dd HH:mm:ss"等
     * @return String 格式转换之后的时间字符串.
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 指定年份和月份，获取这个月有多少天
     *
     * @param year
     * @param month
     * @return
     */
    @NaslLogic(enhance = false)
    public Integer getDaysInMonthzd(Integer year, Integer month) {
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }

    /**
     * 指定年份和月份，获取这个月有多少天
     *
     * @param dateTime yyyy-mm
     * @return
     */
    @NaslLogic(enhance = false)
    public Integer getDaysInMonth(String dateTime) {
        List<String> strings = Arrays.asList(dateTime.split("-"));
        int year = Integer.parseInt(strings.get(0));
        int month = Integer.parseInt(strings.get(1));
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }


    /**
     * 获取指定日期的字符串格式.
     *
     * @param date    需要格式化的时间，不能为空
     * @param pattern 时间格式，例如"yyyy-MM-dd"，"yyyy-MM-dd HH:mm:ss"等
     * @return String 格式转换之后的时间字符串.
     * @since 1.0
     */
    private String getDate(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 获取日期时间字符串，默认格式为（yyyy-MM-dd）.
     *
     * @param date    需要转化的日期时间
     * @param pattern 时间格式，例如"yyyy-MM-dd" "HH:mm:ss" "E"等
     * @return String 格式转换后的时间字符串
     * @since 1.0
     */
    private String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, DateUtil.DATE_FORMAT);
        }
        return formatDate;
    }

    /**
     * 获取日期时间字符串，默认格式为（yyyy-MM-dd HH:mm:ss）.
     *
     * @param date    需要转化的日期时间
     * @param pattern 时间格式，例如"yyyy-MM-dd"，"yyyy-MM-dd HH:mm:ss"等
     * @return String 格式转换后的时间字符串
     * @since 1.0
     */
    private String formatDatehms(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, DateUtil.DATETIME_FORMAT);
        }
        return formatDate;
    }

    /**
     * 获取当前年份字符串.
     *
     * @return String 当前年份字符串，例如 2015
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 获取当前月份字符串.
     *
     * @return String 当前月份字符串，例如 08
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 获取当前天数字符串.
     *
     * @return String 当前天数字符串，例如 11
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 获取当前星期字符串.
     *
     * @return String 当前星期字符串，例如星期二
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 将日期型字符串转换为日期格式. 支持的日期字符串格式包括"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd
     * HH:mm:ss", "yyyy/MM/dd HH:mm"
     *
     * @param str
     * @return Date
     * @since 1.0
     */
    private Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return DateUtils.parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 使用参数Format将字符串转为Date
     */
    private Date parse(String strDate, String pattern) {
        Date d = null;
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            d = df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 获取当前日期与指定日期相隔的天数.
     *
     * @param date 给定的日期
     * @return long 日期间隔天数，正数表示给定日期在当前日期之前，负数表示在当前日期之后
     * @since 1.0
     */
    private long pastDays(Date date) {
        // 将指定日期转换为yyyy-MM-dd格式
        date = parseDate(formatDate(date, DateUtil.DATE_FORMAT));
        // 当前日期转换为yyyy-MM-dd格式
        Date currentDate = parseDate(formatDate(new Date(), DateUtil.DATE_FORMAT));
        long t = 0;
        if (date != null && currentDate != null) {
            t = (currentDate.getTime() - date.getTime()) / DateUtil.MILLISECONDS_PER_DAY;
        }
        return t;
    }

    /**
     * 获取当前日期指定天数之后的日期.
     *
     * @param num 相隔天数
     * @return Date 日期
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String nextDay(Integer num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.DAY_OF_MONTH, curr.get(Calendar.DAY_OF_MONTH) + num);
        return curr.getTime().toString();
    }

    /**
     * 获取当前日期指定月数之后的日期.
     *
     * @param num 间隔月数
     * @return Date 日期
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String nextMonth(Integer num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.MONTH, curr.get(Calendar.MONTH) + num);
        return curr.getTime().toString();
    }

    /**
     * 获取当前日期指定年数之后的日期.
     *
     * @param num 间隔年数
     * @return Date 日期
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String nextYear(Integer num) {
        Calendar curr = Calendar.getInstance();
        curr.set(Calendar.YEAR, curr.get(Calendar.YEAR) + num);
        return curr.getTime().toString();
    }

    /**
     * 获取某年某月，有多少天
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public Integer getMonthDay(Integer year, Integer month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1);    //1为1日
        //java月份从0开始，输入的月份比实际得到的月+1，即month值+1月1日
        //如，输入的是3月，输出的为4月
        c.add(Calendar.DATE, -1);    //-1为减1天，即month值+1月1的前一天，此时可得到想要的正                确的月份
        return c.get(Calendar.DATE);
    }

    /**
     * 获取指定日期前多少天
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @param num        前多少天
     * @return
     */
    @NaslLogic(enhance = false)
    public String getBeforeOneDayByStringDate(Integer year, Integer month, Integer dayOfMonth, Integer num) {
        // 指定日期
        LocalDate date = LocalDate.of(year, month, dayOfMonth);

        // 获取前一天的日期
        LocalDate previousDay = date.minus(Period.ofDays(num));
        return previousDay.toString();
    }

    /**
     * 获取指定日期上多少个月
     *
     * @param year
     * @param month
     * @param num   上多少个月
     * @return
     */
    @NaslLogic(enhance = false)
    public String getBeforeOneMonth(Integer year, Integer month, Integer num) {
        // 指定年份和月份

        // 创建 YearMonth 对象
        YearMonth yearMonth = YearMonth.of(year, month);

        // 获取上一月的 YearMonth 对象
        YearMonth previousMonth = yearMonth.minusMonths(num);

        return previousMonth.toString();
    }


    /**
     * 将 Date 日期转化为 Calendar 类型日期.
     *
     * @param date 给定的时间，若为null，则默认为当前时间
     * @return Calendar Calendar对象
     * @since 1.0
     */
    private Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        // calendar.setFirstDayOfWeek(Calendar.SUNDAY);//每周从周日开始
        // calendar.setMinimalDaysInFirstWeek(1); // 设置每周最少为1天
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar;
    }

    /**
     * 计算两个日期之间相差天数.
     *
     * @param start 计算开始日期
     * @param end   计算结束日期
     * @return long 相隔天数
     * @since 1.0
     */
    private Long getDaysBetween(Date start, Date end) {
        // 将指定日期转换为yyyy-MM-dd格式
        start = parseDate(formatDate(start, DateUtil.DATE_FORMAT));
        // 当前日期转换为yyyy-MM-dd格式
        end = parseDate(formatDate(end, DateUtil.DATE_FORMAT));

        long diff = 0;
        if (start != null && end != null) {
            diff = (end.getTime() - start.getTime()) / DateUtil.MILLISECONDS_PER_DAY;
        }
        return diff;
    }

    /**
     * 计算两个日期之前相隔多少周.
     *
     * @param start 计算开始时间
     * @param end   计算结束时间
     * @return long 相隔周数，向下取整
     * @since 1.0
     */
    private long getWeeksBetween(Date start, Date end) {
        return getDaysBetween(start, end) / DateUtil.DAYS_PER_WEEK;
    }

    /**
     * 获取与指定日期间隔给定天数的日期.
     *
     * @param specifiedDay 给定的字符串格式日期，支持的日期字符串格式包括"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
     *                     "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm"
     * @param num          间隔天数
     * @return String 间隔指定天数之后的日期
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getSpecifiedDayAfter(String specifiedDay, Integer num) {
        Date specifiedDate = parseDate(specifiedDay);
        Calendar c = Calendar.getInstance();
        c.setTime(specifiedDate);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + num);
        String dayAfter = formatDate(c.getTime(), DateUtil.DATE_FORMAT);
        return dayAfter;
    }


    /**
     * 获取两个时间之间的所有日期，已升序排列
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return List<String> 日期集合
     */
    @NaslLogic(enhance = false)
    public List<String> getBetweenDays(String startTime, String endTime) {
        List<String> betweenTime = new ArrayList<String>();
        try {
            Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(startTime);
            Date edate = new SimpleDateFormat("yyyy-MM-dd").parse(endTime);

            SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar sCalendar = Calendar.getInstance();
            sCalendar.setTime(sdate);
            int year = sCalendar.get(Calendar.YEAR);
            int month = sCalendar.get(Calendar.MONTH);
            int day = sCalendar.get(Calendar.DATE);
            sCalendar.set(year, month, day, 0, 0, 0);

            Calendar eCalendar = Calendar.getInstance();
            eCalendar.setTime(edate);
            year = eCalendar.get(Calendar.YEAR);
            month = eCalendar.get(Calendar.MONTH);
            day = eCalendar.get(Calendar.DATE);
            eCalendar.set(year, month, day, 0, 0, 0);

            while (sCalendar.before(eCalendar)) {
                betweenTime.add(outFormat.format(sCalendar.getTime()));
                sCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            betweenTime.add(outFormat.format(eCalendar.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return betweenTime;
    }

    /**
     * 计算两个日期之前间隔的小时数.
     *
     * @param date1 结束时间
     * @param date2 开始时间
     * @return String 相差的小时数，保留一位小数
     * @since 1.0
     */
    private String dateMinus(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return "0";
        }
        Long r = date1.getTime() - date2.getTime();
        DecimalFormat df = new DecimalFormat("#.0");
        double result = r * 1.0 / DateUtil.MILLISECONDS_PER_HOUR;
        return df.format(result);
    }

    /**
     * 获取当前季度 .
     *
     * @return Integer 当前季度数
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public Integer getCurrentSeason() {
        Calendar calendar = Calendar.getInstance();
        Integer month = calendar.get(Calendar.MONTH) + 1;
        int season = 0;
        if (month >= 1 && month <= 3) {
            season = 1;
        } else if (month >= 4 && month <= 6) {
            season = 2;
        } else if (month >= 7 && month <= 9) {
            season = 3;
        } else if (month >= 10 && month <= 12) {
            season = 4;
        }
        return season;
    }

    /**
     * 将以秒为单位的时间转换为其他单位.
     *
     * @param seconds 秒数
     * @return String 例如 16分钟前、2小时前、3天前、4月前、5年前等
     * @since 1.0
     */
    @NaslLogic(enhance = false)
    public String getIntervalBySeconds(Long seconds) {
        StringBuffer buffer = new StringBuffer();
        if (seconds < SECONDS_PER_MINUTE) {
            buffer.append(seconds).append("秒前");
        } else if (seconds < SECONDS_PER_HOUR) {
            buffer.append(seconds / SECONDS_PER_MINUTE).append("分钟前");
        } else if (seconds < SECONDS_PER_DAY) {
            buffer.append(seconds / SECONDS_PER_HOUR).append("小时前");
        } else if (seconds < SECONDS_PER_MONTH) {
            buffer.append(seconds / SECONDS_PER_DAY).append("天前");
        } else if (seconds < SECONDS_PER_YEAR) {
            buffer.append(seconds / SECONDS_PER_MONTH).append("月前");
        } else {
            buffer.append(seconds / DateUtil.SECONDS_PER_YEAR).append("年前");
        }
        return buffer.toString();
    }

    /**
     * getNowTimeBefore(记录时间相当于目前多久之前)
     *
     * @param seconds 秒
     * @return
     * @throws @since 1.0
     * @author rlliu
     */
    @NaslLogic(enhance = false)
    public String getNowTimeBefore(Long seconds) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("上传于");
        if (seconds < 3600) {
            buffer.append((long) Math.floor(seconds / 60.0)).append("分钟前");
        } else if (seconds < 86400) {
            buffer.append((long) Math.floor(seconds / 3600.0)).append("小时前");
        } else if (seconds < 604800) {
            buffer.append((long) Math.floor(seconds / 86400.0)).append("天前");
        } else if (seconds < 2592000) {
            buffer.append((long) Math.floor(seconds / 604800.0)).append("周前");
        } else if (seconds < 31104000) {
            buffer.append((long) Math.floor(seconds / 2592000.0)).append("月前");
        } else {
            buffer.append((long) Math.floor(seconds / 31104000.0)).append("年前");
        }
        return buffer.toString();
    }


    /**
     * 获取两个日期相差的天数  方法一
     *
     * @param startDate 开始日期的字符串  yyyy-MM-dd
     * @param endDate   结束日期的字符串  yyyy-MM-dd
     * @return 相差天数，如果解析失败则返回-1
     **/
    @NaslLogic(enhance = false)
    public Long subDaysByDate(String startDate, String endDate) {
        long sub;
        try {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            LocalDate start = LocalDate.parse(startDate, dateTimeFormatter);
            LocalDate end = LocalDate.parse(endDate, dateTimeFormatter);
            sub = Duration.between(LocalDateTime.of(start, LocalTime.of(0, 0, 0)),
                    LocalDateTime.of(end, LocalTime.of(0, 0, 0))).toDays();
        } catch (DateTimeParseException e) {
            sub = -1;
        }
        return sub;
    }


    /**
     * getMonthsBetween(查询两个日期相隔的月份)
     *
     * @param startDate 开始日期1 (格式yyyy-MM-dd)
     * @param endDate   截止日期2 (格式yyyy-MM-dd)
     * @return
     */
    private Integer getMonthsBetween(Date startDate, Date endDate) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(parseDate(startDate));
        c2.setTime(parseDate(endDate));
        int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        int month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        return Math.abs(year * 12 + month);
    }

//    /**
//     * getDayOfWeek(获取当前日期是星期几)
//     *
//     * @param dateStr 日期
//     * @return 星期几
//     */
//    @NaslLogic(enhance = false)
//    public String getDayOfWeek(String dateStr) {
//        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
//        Date date = parseDate(dateStr);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        int num = calendar.get(Calendar.DAY_OF_WEEK) - 1;
//        return weekOfDays[num];
//    }

    /**
     * sns 格式 如几秒前，几分钟前，几小时前，几天前，几个月前，几年后， ... 精细，类如某个明星几秒钟之前发表了一篇微博
     *
     * @param createTime
     * @return
     */
    @NaslLogic(enhance = false)
    public String snsFormat(Long createTime) {
        long now = System.currentTimeMillis() / 1000;
        long differ = now - createTime / 1000;
        String dateStr = "";
        if (differ <= 60) {
            dateStr = "刚刚";
        } else if (differ <= 3600) {
            dateStr = (differ / 60) + "分钟前";
        } else if (differ <= 3600 * 24) {
            dateStr = (differ / 3600) + "小时前";
        } else if (differ <= 3600 * 24 * 30) {
            dateStr = (differ / (3600 * 24)) + "天前";
        } else {
            Date date = new Date(createTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = sdf.format(date);
        }
        return dateStr;
    }

    /**
     * 得到UTC时间，类型为字符串，格式为"yyyy-MM-dd HH:mm" 如果获取失败，返回null
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getUTCTimeStr() {
        StringBuffer UTCTimeBuffer = new StringBuffer();
        // 1、取得本地时间：
        Calendar cal = Calendar.getInstance();
        // 2、取得时间偏移量：
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        // 3、取得夏令时差：
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        // 4、从本地时间里扣除这些差量，即可以取得UTC时间：
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        UTCTimeBuffer.append(year).append("-").append(month).append("-").append(day);
        UTCTimeBuffer.append(" ").append(hour).append(":").append(minute);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.parse(UTCTimeBuffer.toString());
            return UTCTimeBuffer.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 在日期上增加数个整年
     */
    private Date addYear(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, n);
        return cal.getTime();
    }

    /**
     * 在日期上增加数个整月
     */
    private Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * 在日期上增加数个整日(获取上一天下一天，n为负数则是减少数日)
     */
    private Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, n);
        return cal.getTime();
    }

    /**
     * 在日期上增加数个小时(获取上一个小时或下一个小时,n为负数则是减少数小时)
     */
    private Date addHour(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, n);
        return cal.getTime();
    }

    /**
     * 获取明日0点时刻
     * <p>
     * 比如今天是2019-02-01，调用这个方法会返回2019-02-02
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getTomorrowDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 获取昨天0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2021-11-25
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 获取当天日期0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2021-11-26
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 获取当月1号0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2021-11-01
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getMonthFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 获取上月1号0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2021-11-01
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getLastMonthFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 获取当前月离之前N月1号0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2021-n-01
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getNMonthFirstDate(Integer n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, n);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 获取上月1号0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2021-11-01
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getNextMonthFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 获取当年1月1号0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2021-01-01
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getYearFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 在日期上增加数个分钟(n为负数则是减少数分钟)
     */
    private Date addMinute(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, n);
        return cal.getTime();
    }

    /**
     * 获取去年1月1号0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2020-01-01
     *
     * @return
     */
    @NaslLogic(enhance = false)
    public String getLastYearFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, -1);
        cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        return cal.getTime().toString();
    }

    /**
     * 获取去年1月1号0点时刻
     * <p>
     * 比如今天是2021-11-26，调用这个方法会返回2020-01-01
     *
     * @return
     */
    private Date getNextYearFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, 1);
        cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        return cal.getTime();
    }

    /**
     * 时间格式化到天
     * <p>
     * 比如时间是2021-11-26 13:42:00，调用这个方法会返回2021-11-26 00:00:00
     *
     * @return
     */
    private Date formatDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, 1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return cal.getTime();
    }

    /**
     * 根据起止时间获取环比开始时间
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 环比开始时间
     */
    private Date getChainRatioStartDate(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        return new Date(startDate.getTime() * 2 - endDate.getTime());
    }


    /**
     * 秒时间
     */
    @NaslLogic(enhance = false)
    public String secToTime(Integer seconds) {
        Integer hour = seconds / 3600;
        Integer minute = (seconds - hour * 3600) / 60;
        Integer second = (seconds - hour * 3600 - minute * 60);

        StringBuffer str = new StringBuffer();
        if (hour > 0) {
            str.append(hour + "h");
        }
        if (minute > 0) {
            str.append(minute + "min");
        }
        if (second >= 0) {
            str.append(second + "s");
        }
        return str.toString();
    }


    /**
     * 时间格式
     *
     * @param num 处理秒时间格式
     * @return {@code String}
     */
    @NaslLogic(enhance = false)
    public String timeFormat(Integer num) {
        String retStr = null;
        if (num >= 0 && num < 10) {
            retStr = "0" + Integer.toString(num);
        } else {
            retStr = "" + num;
        }
        return retStr;
    }


    /**
     * 时间分片
     *
     * @param dateType 日期类型
     * @param start    开始
     * @param end      结束
     * @return {@code List<String>}
     */
    private List<String> cutDate(String dateType, Date start, Date end) {
        try {
            String dBegin = getDate(start, "yyyy-MM-dd HH:mm:ss");
            String dEnd = getDate(end, "yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date cutBegin = sdf.parse(dBegin);
            Date cutEnd = sdf.parse(dEnd);
            return findDates(dateType, cutBegin, cutEnd);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    private List<String> findDates(String dateType, Date dBegin, Date dEnd) throws Exception {
        List<String> listDate = new ArrayList<>();
        listDate.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dBegin.getTime()));
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (calEnd.after(calBegin)) {
            switch (dateType) {
                case "M":
                    calBegin.add(Calendar.MONTH, 1);
                    break;
                case "D":
                    calBegin.add(Calendar.DAY_OF_YEAR, 1);
                    break;
                case "H":
                    calBegin.add(Calendar.HOUR, 1);
                    break;
                case "N":
                    calBegin.add(Calendar.MINUTE, 10);
                    break;
                case "Y":
                    calBegin.add(Calendar.YEAR, 1);
                default:
            }
            if (calEnd.after(calBegin)) {
                listDate.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calBegin.getTime()));
            }
        }
        return listDate;
    }

    /**
     * 整日时间分片
     * DAY,MONTH,YEAR,CUSTOM
     *
     * @param dateType 日期类型
     * @param start    开始
     * @param end      结束
     * @return {@code List<String>}
     */
    private List<String> cutDates(String dateType, Date start, Date end, int n) {
        try {
            String dBegin = getDate(start, "yyyy-MM-dd HH:mm:ss");
            String dEnd = getDate(end, "yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date cutBegin = sdf.parse(dBegin);
            Date cutEnd = sdf.parse(dEnd);
            return findDate(dateType, cutBegin, cutEnd, n);
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    private List<String> findDate(String dateType, Date dBegin, Date dEnd, int n) throws Exception {
        List<String> listDate = new ArrayList<>();
        listDate.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dBegin.getTime()));
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);
        while (calEnd.after(calBegin)) {
            switch (dateType) {
                case "MONTH":
                    calBegin.add(Calendar.MONTH, 1);
                    break;
                case "DAY":
                    calBegin.add(Calendar.DAY_OF_YEAR, 1);
                    break;
                case "CUSTOM":
                    calBegin.add(Calendar.DAY_OF_YEAR, n);
                    break;
                case "YEAR":
                    calBegin.add(Calendar.YEAR, 1);
                default:
            }
            if (calEnd.after(calBegin)) {
                listDate.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calBegin.getTime()));
            }
        }
        return listDate;
    }

    /**
     * 判断当前时间是否在开始事假和结束时间之间
     *
     * @param nowTime   现在时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return boolean
     */
    private boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 指定开始和结束日，获取后面每一天的日期
     *
     * @param startDate 日期格式默认开始日yyyy-MM-dd
     * @param endDate   日期格式默认结束日yyyy-MM-dd
     * @param pattern   自定义日期格式DATE_FORMAT=yyyy-MM-dd
     * @return
     */
    @NaslLogic(enhance = false)
    public List<String> findEveryDay(String startDate, String endDate, String pattern) {

        Date startParse = null;
        Date endParse = null;
        if (pattern != null) {
            startParse = parse(startDate, pattern);
            endParse = parse(endDate, pattern);
        } else {
            startParse = parse(startDate, DATE_FORMAT);
            endParse = parse(endDate, DATE_FORMAT);
        }
        System.out.println(startParse);
        System.out.println(endParse);
        // 用于存放日期集合
        List<String> dateList = new ArrayList<>();

        dateList.add(startDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startParse);

        while (endParse.after(calendar.getTime())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(formatDate(calendar.getTime()));
        }

        return dateList;
    }
}