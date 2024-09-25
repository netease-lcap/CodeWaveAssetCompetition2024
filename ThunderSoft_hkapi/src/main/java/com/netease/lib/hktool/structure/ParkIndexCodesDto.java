package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class ParkIndexCodesDto {

    public List<String> parkIndexCodes;

    public List<String> getParkIndexCodes() {
        return parkIndexCodes;
    }

    public void setParkIndexCodes(List<String> parkIndexCodes) {
        this.parkIndexCodes = parkIndexCodes;
    }
}
