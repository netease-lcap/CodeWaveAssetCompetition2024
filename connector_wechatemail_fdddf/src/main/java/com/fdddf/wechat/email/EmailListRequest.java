package com.fdddf.wechat.email;

import com.fdddf.wechat.implement.IWeixinRequest;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailListRequest implements IWeixinRequest {
    /**
     * 开始时间，unix时间戳
     */
    public Long begin_time;
    /**
     * 结束时间，unix时间戳
     */
    public Long end_time;
    /**
     * 上一次调用时返回的next_cursor，第一次拉取可以不填
     */
    public String cursor;
    /**
     * 期望请求的数据量，默认值为100，最大值为1000
     */
    public Integer limit = 100;

    public EmailListRequest() {}

    public EmailListRequest(Long begin_time, Long end_time, String cursor, Integer limit) {
        this.begin_time = begin_time;
        this.end_time = end_time;
        this.cursor = cursor;
        this.limit = limit;
    }
}
