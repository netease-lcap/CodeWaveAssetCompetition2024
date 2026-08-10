package com.wgx.cache.jvm.model;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CacheDataWrapper {
    //缓存内容
    private  String data;
    //缓存过期时间
    private  Long delay;
    //缓存过期时间单位
    private TimeUnit unit;

    public CacheDataWrapper() {
    }

    public CacheDataWrapper(String data, Long delay, TimeUnit unit) {
        this.data = data;
        this.delay = delay;
        this.unit = unit;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheDataWrapper that = (CacheDataWrapper) o;
        return Objects.equals(data, that.data) && Objects.equals(delay, that.delay) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, delay, unit);
    }

    @Override
    public String toString() {
        return "CacheDataWrapper{" +
                "data='" + data + '\'' +
                ", delay=" + delay +
                ", unit=" + unit +
                '}';
    }
}
