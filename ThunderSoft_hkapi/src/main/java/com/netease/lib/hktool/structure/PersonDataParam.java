package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

/**
 * 人员数据列表
 */
@NaslStructure
public class PersonDataParam
{
    public List<String> indexCodes;

    public String personDataType;

    public List<String> getIndexCodes() {
        return indexCodes;
    }

    public void setIndexCodes(List<String> indexCodes) {
        this.indexCodes = indexCodes;
    }

    public String getPersonDataType() {
        return personDataType;
    }

    public void setPersonDataType(String personDataType) {
        this.personDataType = personDataType;
    }
}