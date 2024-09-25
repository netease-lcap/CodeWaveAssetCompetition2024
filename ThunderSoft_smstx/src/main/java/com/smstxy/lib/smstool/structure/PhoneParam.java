package com.smstxy.lib.smstool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

/**
 * 相加入参，声明为code-wave认识的结构体
 */
@NaslStructure
public class PhoneParam {
    /**
     * 返回参数
     */
    public String phone;
    /**
     * 结果名称
     */
    public List<String> param;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public List<String> getParam() {
        return param;
    }

    public void setParam(List<String> param) {
        this.param = param;
    }
}
