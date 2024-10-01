package com.fdddf.wechat.email;

import com.alibaba.fastjson.annotation.JSONField;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailSchedule {
    /**
     * 会议ID (修改/取消会议必须带上schedule_id)
     */
    public String schedule_id;
    /**
     * 会议方法：request-请求（不传schedule_id时是创建会议，传了是修改会议）,cancel-取消会议（必须带上schedule_id） 默认为request
     */
    @JSONField(name = "method")
    public String req_method = "request";
    /**
     * 地点
     */
    public String location;
    /**
     * 会议开始时间，Unix时间戳
     */
    public Integer start_time;
    /**
     * 会议结束时间，Unix时间戳
     */
    public Integer end_time;

    /**
     * 重复和提醒相关字段
     */
    public EmailScheduleReminder reminders;

    public EmailSchedule() {}

    public EmailSchedule(String schedule_id, String req_method, String location, Integer start_time, Integer end_time,
                         EmailScheduleReminder reminders) {
        this.schedule_id = schedule_id;
        this.req_method = req_method;
        this.location = location;
        this.start_time = start_time;
        this.end_time = end_time;
        this.reminders = reminders;
    }
}
