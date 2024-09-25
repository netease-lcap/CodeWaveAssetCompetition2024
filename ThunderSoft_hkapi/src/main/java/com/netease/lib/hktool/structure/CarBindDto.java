package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class CarBindDto {
    public String vehicleIds;

    public String categoryCode;

    public String regionIndexCode;

    public Integer operation;

    public String getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(String vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getRegionIndexCode() {
        return regionIndexCode;
    }

    public void setRegionIndexCode(String regionIndexCode) {
        this.regionIndexCode = regionIndexCode;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }
}