package com.fdddf.wechat.email;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailMeetingOption {
    /**
     * 入会密码，仅可输入4-6位纯数字
     */
    public String password;
    /**
     * 是否自动录制 0：未开启自动录制，1：开启自动本地录制，2：开启自动云录制；默认不开启
     */
    public Integer auto_record;
    /**
     * 是否开启等候室
     */
    public Boolean enable_waiting_room;
    /**
     * 否允许成员在主持人进会前加入
     */
    public Boolean allow_enter_before_host;
    /**
     * 是否限制成员入会 0:所有人可入会 2:仅企业内部用户可入会；默认所有人可入会
     */
    public Integer enter_restraint;
    /**
     * 是否开启屏幕水印
     */
    public Boolean enable_screen_watermark;
    /**
     * 成员入会时是否静音 1:开启；0:关闭；2:超过6人后自动开启静音。默认超过6人自动开启静音
     */
    public Integer enable_enter_mute;
    /**
     * 会议开始时是否提醒 1:不提醒 2:仅提醒主持人 3:提醒所有成员入会; 默认仅提醒主持人
     */
    public Integer remind_scope;
    /**
     * 水印类型 0:单排水印 1:多排水印；默认单排水印
     */
    public Integer water_mark_type;

    public EmailMeetingOption() {}

    public EmailMeetingOption(String password, Integer auto_record, Boolean enable_waiting_room, Boolean allow_enter_before_host, 
                  Integer enter_restraint, Boolean enable_screen_watermark, Integer enable_enter_mute,
                  Integer remind_scope, Integer water_mark_type) {
        this.password = password;
        this.auto_record = auto_record;
        this.enable_waiting_room = enable_waiting_room;
        this.allow_enter_before_host = allow_enter_before_host;
        this.enter_restraint = enter_restraint;
        this.enable_screen_watermark = enable_screen_watermark;
        this.enable_enter_mute = enable_enter_mute;
        this.remind_scope = remind_scope;
        this.water_mark_type = water_mark_type;
    }
}
