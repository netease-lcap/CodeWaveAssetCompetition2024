package com.hq.unitApi.service;


import cn.hutool.core.convert.Convert;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class UnitAPIService {

    //[5432, 业主单位, 分段总包]/"
    @NaslLogic(enhance = false)
    public String listConverToString(List<String> param) {
        return Convert.toStr(param).replace("[", "").replace("]", "");
    }

    /**
     * 获取日期是周几（int）
     *
     * @param dateStr
     * @return
     */
    @NaslLogic(enhance = false)
    public Integer getDayOfWeek(String dateStr) {
        LocalDate ld = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return ld.getDayOfWeek().getValue();
    }

    /**
     * 获取日期是周几（String）
     *
     * @param dateStr
     * @return
     */
    @NaslLogic(enhance = false)
    public String getDayOfWeekName(String dateStr) {
        String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        int week = getDayOfWeek(dateStr) - 1;
        if (week < 0) {
            week = 0;
        }
        return weekDays[week];
    }
}

