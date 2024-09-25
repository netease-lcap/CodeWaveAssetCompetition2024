package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class SortObjectParam
{
    public String fieled;

    public String sort;

    public String getFieled() {
        return fieled;
    }

    public void setFieled(String fieled) {
        this.fieled = fieled;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
