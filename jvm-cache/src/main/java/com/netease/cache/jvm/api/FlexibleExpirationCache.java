package com.netease.cache.jvm.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FlexibleExpirationCache<K, V> {
    private final Cache<K, V> cache;
    private final ConcurrentHashMap<K, Long> expirationMap = new ConcurrentHashMap<>();

    public FlexibleExpirationCache(Long maxSize) {
        this.cache =  CacheBuilder.newBuilder()
                .removalListener(notification -> {
                    if (notification.getKey() != null) {
                        expirationMap.remove(notification.getKey());
                    }
                })
                .maximumSize(maxSize)
                .build();
    }

    public void put(K key, V value, Long expireAfterWriteSeconds) {
        cache.put(key, value);
        expirationMap.put(key, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expireAfterWriteSeconds));
    }

    public V get(K key) {
        if (isExpired(key)) {
            invalidate(key);
            return null;
        }
        return cache.getIfPresent(key);
    }

    public List<K> getAllKeys() {
        return new ArrayList<>(cache.asMap().keySet());
    }

    public V getValue(K key, Callable<V> loader) {
        try {
            return cache.get(key, loader);
        } catch (ExecutionException e) {
            // 处理加载异常
            throw new RuntimeException(e.getCause());
        }
    }

    public boolean isExpired(K key) {
        Long expiryTime = expirationMap.get(key);
        return expiryTime != null && System.currentTimeMillis() > expiryTime;
    }

    public void invalidate(K key) {
        cache.invalidate(key);
        expirationMap.remove(key);
    }

    public void invalidateAll() {
        cache.invalidateAll();
        expirationMap.clear();
    }
}
