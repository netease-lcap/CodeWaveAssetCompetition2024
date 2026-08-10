package com.wgx.cache.jvm.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.wgx.cache.jvm.config.JvmCacheConfig;
import com.wgx.cache.jvm.model.CacheDataWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JVM缓存管理器
 * 负责初始化和配置Caffeine缓存
 */
@Configuration
public class JvmCacheManager {

    @Autowired
    private JvmCacheConfig cacheConfig;

    @Bean("basicCommonCache")
    public Cache<String, CacheDataWrapper> initCache() {
        return Caffeine.newBuilder()
                // 初始的缓存空间大小
                .initialCapacity(Math.toIntExact(cacheConfig.initialCapacity))
                // 缓存的最大条数
                .maximumSize(cacheConfig.maximumSize)
                // key过期策略
                .expireAfter(new Expiry<Object, CacheDataWrapper>() {
                    //创建缓存设置过期时间，当TimeUnit参数为空时，不设置过期
                    @Override
                    public long expireAfterCreate(Object o , CacheDataWrapper cw, long l) {
                        if (cw.getUnit()!=null){
                            return cw.getUnit().toNanos(cw.getDelay());
                        }
                        return l;
                    }
                    //更新缓存（相同key）时，取新的过期时间设置
                    @Override
                    public long expireAfterUpdate(Object o, CacheDataWrapper cw, long l, long l1) {
                        if (cw.getUnit()!=null){
                            return cw.getUnit().toNanos(cw.getDelay());
                        }
                        return l;
                    }
                    //读完缓存不能影响过期时间
                    @Override
                    public long expireAfterRead(Object o, CacheDataWrapper cw, long l, long l1) {
                        return l1;
                    }
                })
                .build();
    }
}