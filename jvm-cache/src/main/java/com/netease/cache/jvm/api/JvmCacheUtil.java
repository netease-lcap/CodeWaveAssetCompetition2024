package com.netease.cache.jvm.api;

import com.netease.lowcode.core.EnvironmentType;
import com.netease.lowcode.core.annotation.Environment;
import com.netease.lowcode.core.annotation.NaslConfiguration;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;

@Component
public class JvmCacheUtil {

    private static FlexibleExpirationCache<String, String> cache;

    /**
     * 缓存空间大小
     * 类型为Long
     */
    @Value("${cacheMaximumSize}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "100"),
            @Environment(type = EnvironmentType.ONLINE, value = "100")})
    public Long cacheMaximumSize;


    @PostConstruct
    public void init() {
        cache = new FlexibleExpirationCache<>(cacheMaximumSize);
    }

    /**
     * 添加缓存
     *
     * @param key 键
     * @param value 值
     * @param expireAfterWriteSeconds 过期时间（秒）
     */
    @NaslLogic
    public String setCache(String key, String value, Long expireAfterWriteSeconds) {
        assertNotNull(key, value, expireAfterWriteSeconds);
        cache.put(key, value, expireAfterWriteSeconds);
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    @NaslLogic
    public String getCache(String key) {
        assertNotNull(key);
        return cache.get(key);
    }

    /**
     * 获取缓存，缓存不存在则执行function， 并将查询的值放到缓存中
     */
    @NaslLogic
    public String getOrComputeAndSet(String key, Long expireAfterWriteSeconds, Function<String, String> functionStr) {
        assertNotNull(key, expireAfterWriteSeconds, functionStr);
        String value = getCache(key);
        if (value != null) {
            return value;
        }

        String str = functionStr.apply(null);
        if (str != null) {
            setCache(key, str, expireAfterWriteSeconds);
        }
        return str;
    }

    /**
     * 是否过期
     *
     * @param key 键
     * @return true/false
     */
    @NaslLogic
    public Boolean isExpired(String key) {
        assertNotNull(key);
        return cache.isExpired(key);
    }

    /**
     * 清除缓存
     *
     * @param key 键
     */
    @NaslLogic
    public Boolean invalidate(String key) {
        assertNotNull(key);
        cache.invalidate(key);
        return true;
    }

    /**
     * 清除所有缓存
     */
    @NaslLogic
    public Boolean invalidateAll() {
        cache.invalidateAll();
        return true;
    }

    /**
     * 查询缓存中所有的key
     */
    @NaslLogic
    public List<String> keys() {
        return cache.getAllKeys();
    }

    private void assertNotNull(Object object) {
        if (null == object) {
            throw new RuntimeException("INPUT_INVALID");
        }
    }

    private void assertNotNull(Object... objects){
        for (Object object : objects) {
            assertNotNull(object);
        }
    }
}
