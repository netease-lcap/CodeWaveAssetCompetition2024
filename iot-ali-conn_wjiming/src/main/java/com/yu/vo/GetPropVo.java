package com.yu.vo;

import com.netease.lowcode.core.annotation.NaslStructure;
import lombok.ToString;

import java.util.List;

/**
 * @author 余卫青
 * @version 1.0.0
 * @date 2024/4/21 14:35
 **/
@NaslStructure
@ToString
public class GetPropVo {
    /**
     * 是否有下一页
     */
    public Boolean NextValid;
    /**
     * 下一记录开始时间
     */
    public Long NextTime;
    public List<PropertyInfo> propertyInfos;


}
