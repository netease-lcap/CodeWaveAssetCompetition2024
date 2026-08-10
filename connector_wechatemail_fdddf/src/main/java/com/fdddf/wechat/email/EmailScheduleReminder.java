package com.fdddf.wechat.email;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class EmailScheduleReminder {
    /**
     * 是否有提醒 0-不提醒 1-提醒
     */
    public Integer is_remind;
    /**
     * 日程开始（start_time）前多少分钟提醒，当is_remind=1时有效。例如：15表示日程开始前15分钟提醒,-15表示日程开始后15分钟提醒
     */
    public Integer remind_before_event_mins;
    /**
     * 是否重复 0-否 1-是
     */
    public Integer is_repeat;
    /**
     * 是否自定义重复 0-否 1-是。当is_repeat为1时有效。
     */
    public Integer is_custom_repeat;
    public Integer timezone;
    /**
     * 重复间隔
     */
    public Integer repeat_interval;
    /**
     * 重复类型，当is_repeat=1时有效。目前支持如下类型：0 - 每日, 1 - 每周, 2 - 每月, 5 - 每年
     */
    public Integer repeat_type;
    /**
     * 每周周几重复, 取值范围：1 ~ 7，分别表示周一至周日
     */
    public List<Integer> repeat_day_of_week;
    /**
     * 每月哪几天重复, 取值范围：1 ~ 31，分别表示1~31号
     */
    public List<Integer> repeat_day_of_month;
    /**
     * 每年哪几个月重复, 取值范围：1 ~ 12
     */
    public List<Integer> repeat_day_of_year;
    /**
     * 重复结束时刻，Unix时间戳，当is_repeat=1时有效。不填或填0表示一直重复
     */
    public Long repeat_until;

    public EmailScheduleReminder() {}

    public EmailScheduleReminder(Integer is_remind, Integer remind_before_event_mins, Integer is_repeat,
            Integer is_custom_repeat, Integer timezone, Integer repeat_interval, Integer repeat_type,
            List<Integer> repeat_day_of_week, List<Integer> repeat_day_of_month, List<Integer> repeat_day_of_year,
            Long repeat_until) {
        this.is_remind = is_remind;
        this.remind_before_event_mins = remind_before_event_mins;
        this.is_repeat = is_repeat;
        this.is_custom_repeat = is_custom_repeat;
        this.timezone = timezone;
        this.repeat_interval = repeat_interval;
        this.repeat_type = repeat_type;
        this.repeat_day_of_week = repeat_day_of_week;
        this.repeat_day_of_month = repeat_day_of_month;
        this.repeat_day_of_year = repeat_day_of_year;
        this.repeat_until = repeat_until;
    }

}
