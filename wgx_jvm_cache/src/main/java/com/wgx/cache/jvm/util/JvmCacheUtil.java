package com.wgx.cache.jvm.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.netease.lowcode.core.annotation.NaslLogic;
import com.wgx.cache.jvm.exception.CacheException;
import com.wgx.cache.jvm.model.CacheDataWrapper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.function.Function;

@Component
public class JvmCacheUtil {

    // getLogger参数统一使用LCAP_EXTENSION_LOGGER
    private static final Logger logger = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    @Autowired
    @Qualifier("basicCommonCache")
    public Cache<String, CacheDataWrapper> basicCommonCache;

    //用于连接外层key和hash的Key，作为新key存储hash的Value
    private static final String SEPARATOR = ":";

    /**
     * 获取所有缓存键
     * @return 所有缓存键的集合，如果缓存为空或未初始化则返回空列表
     */
    @NaslLogic
    public List<String> getAllKeys() {
        checkCacheInitialized("获取所有缓存键");

        // 获取缓存中所有键的集合
        Set<String> allKeys = basicCommonCache.asMap().keySet();
        if (allKeys.isEmpty()) {
            return Collections.emptyList();
        }
        // 过滤掉已经过期的键
        return allKeys.stream()
                .filter(key -> basicCommonCache.getIfPresent(key) != null)
                .collect(Collectors.toList());
    };
 
    /**
     * 获取缓存中的所有键值对
     * @return 包含所有键值对的Map，如果缓存为空或未初始化则返回空Map
     */
    @NaslLogic
    public Map<String, String> getAllCache() {
        checkCacheInitialized("获取所有缓存");
        
        Map<String, CacheDataWrapper> cacheMap = basicCommonCache.asMap();
        if (cacheMap.isEmpty()) {
            logger.info("获取所有缓存时，缓存为空");
            return Collections.emptyMap();
        }
        
        try {
            return cacheMap.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> String.valueOf(e.getValue().getData()),
                    (v1, v2) -> v2,
                    LinkedHashMap::new
                ));
        } catch (Exception e) {
            logger.error("获取所有缓存异常", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 检查键是否存在
     * @param key 缓存键
     * @return 如果键存在返回true，否则返回false
     */
    @NaslLogic
    public Boolean exist(final String key) {
        checkCacheInitialized("检查键是否存在");

        if (StringUtils.isBlank(key)) {
            logger.warn("检查键是否存在时, key为空或空白");
            return false;
        }
        
        return basicCommonCache.getIfPresent(key) != null;
    }

    /**
     * 判断哈希键是否存在
     *
     * @param key 主键
     * @param hashField 哈希字段
     * @return 如果存在返回true，否则返回false
     */
    @NaslLogic
    public Boolean existHash(final String key, final String hashField) {
        checkCacheInitialized("检查哈希键是否存在");

        if (StringUtils.isBlank(key) || StringUtils.isBlank(hashField)) {
            logger.warn("检查哈希键是否存在时,key或hashField为空或空白");
            return false;
        }

        return basicCommonCache.asMap().containsKey(key + SEPARATOR + hashField);
    }

     /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @return 存入成功返回true，否则返回false
     * @throws CacheException 如果存入缓存时发生异常
     */
    @NaslLogic
    public Boolean setCache(final String key, final String value) {
        checkCacheInitialized("设置缓存");

        if (key == null || key.isEmpty()) {
            logger.warn("设置缓存时,key为空或空白");
            return false;
        }
        
        try {
            basicCommonCache.put(key, new CacheDataWrapper(value, null, null));
            logger.info("设置缓存成功：键 = {}, 值 = {}", key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失败：键 = {}, 值 = {}", key, value, e);
            throw new CacheException("设置缓存异常", e);
        }
    }

    /**
     * 设置带过期时间的缓存
     * @param key 缓存键
     * @param value 缓存值
     * @param expire 过期时间（毫秒）
     * @return 设置成功返回true，否则返回false
     */
    @NaslLogic
    public Boolean setCacheWithExpire(final String key, final String value, final Long expire) {
        checkCacheInitialized("设置带过期时间的缓存");
        
        if (StringUtils.isBlank(key) || expire == null || expire < 0) {
            logger.warn("设置带过期时间的缓存时,key为空或expire无效: key = {}, expire = {}", key, expire);
            return false;
        }
        try {
            basicCommonCache.put(key, new CacheDataWrapper(value, expire, TimeUnit.MILLISECONDS));
            logger.info("设置缓存成功：键 = {}, 值 = {}, 过期时间 = {} 毫秒", key, value, expire);
            return true;
        } catch (Exception e) {
            logger.error("设置缓存失败：键 = {}, 值 = {}, 过期时间 = {} 毫秒", key, value, expire, e);
            throw new CacheException("设置带过期时间的缓存异常", e);
        }
    }

    /**
     * 设置哈希缓存
     * @param key 缓存键
     * @param hashField 哈希字段
     * @param value 缓存值
     * @return 设置成功返回true，否则返回false
     * @throws CacheException 如果设置缓存时发生异常
     */
    @NaslLogic
    public Boolean setHashCache(final String key, final String hashField, final String value) {
        checkCacheInitialized("设置哈希缓存");
        
        if (StringUtils.isBlank(key) || StringUtils.isBlank(hashField)) {
            logger.warn("设置哈希缓存时,key或hashField为空或空白");
            return false;
        }
        try {
            basicCommonCache.put(key + SEPARATOR + hashField, new CacheDataWrapper(value, null , null));
            logger.info("哈希设置缓存成功：键 = {}, 字段 = {}, 值 = {}", key, hashField, value);
            return true;
        } catch (Exception e) {
            logger.error("哈希设置缓存失败：键 = {}, 字段 = {}, 值 = {}", key, hashField, value, e);
            throw new CacheException("设置哈希缓存异常", e);
        }
    }

    /**
     * 设置带过期时间的哈希缓存
     * @param key 缓存键
     * @param hashField 哈希字段
     * @param value 缓存值
     * @param expire 过期时间（毫秒）
     * @return 设置成功返回true，否则返回false
     * @throws CacheException 如果设置缓存时发生异常
     */
    @NaslLogic
    public Boolean setHashCacheWithExpire(final String key, final String hashField, final String value, final Long expire) {
        checkCacheInitialized("设置带过期时间的哈希缓存");
        
        if (StringUtils.isBlank(key) || StringUtils.isBlank(hashField) || expire == null || expire < 0) {
            logger.warn("设置带过期时间的哈希缓存时,参数无效: key = {}, hashField = {}, expire = {}", key, hashField, expire);
            return false;
        }
        try {
            basicCommonCache.put(key + SEPARATOR + hashField, new CacheDataWrapper(value, expire, TimeUnit.MILLISECONDS));
            logger.info("哈希设置缓存成功：键 = {}, 字段 = {}, 值 = {}, 过期时间 = {} 毫秒", key, hashField, value, expire);
            return true;
        } catch (Exception e) {
            logger.error("哈希设置缓存失败：键 = {}, 字段 = {}, 值 = {}, 过期时间 = {} 毫秒", key, hashField, value, expire, e);
            throw new CacheException("设置带过期时间的哈希缓存异常", e);
        }
    }

    /**
     * 获取缓存
     * @param key 缓存键
     * @return 缓存值，如果不存在或已过期返回null
     * @throws CacheException 如果获取缓存时发生异常
     */
    @NaslLogic
    public String getCache(final String key) {
        checkCacheInitialized("获取缓存");
        
        if (StringUtils.isBlank(key)) {
            logger.warn("获取缓存失败: key为空或空白");
            return null;
        }
        
        try {
            CacheDataWrapper wrapper = basicCommonCache.getIfPresent(key);
            if (wrapper != null) {
                String value = wrapper.getData();
                logger.info("获取缓存成功：键 = {}, 值 = {}", key, value);
                return value;
            } else {
                logger.info("获取缓存失败：键 = {} 不存在或已过期", key);
                return null;
            }
        } catch (Exception e) {
            logger.error("获取缓存异常：键 = {}", key, e);
            throw new CacheException("获取缓存异常", e);
        }
    }

    /**
     * 获取哈希缓存
     * @param key 缓存键
     * @param hashField 哈希字段
     * @return 缓存值，如果不存在或已过期返回null
     * @throws CacheException 如果获取缓存时发生异常
     */
    @NaslLogic
    public String getHashCache(final String key, final String hashField) {
        checkCacheInitialized("获取哈希缓存");
        
        if (StringUtils.isBlank(key) || StringUtils.isBlank(hashField)) {
            logger.warn("获取哈希缓存失败: key或hashField为空或空白");
            return null;
        }
        
        try {
            CacheDataWrapper wrapper = basicCommonCache.getIfPresent(key + SEPARATOR + hashField);
            if (wrapper != null) {
                String value = wrapper.getData();
                logger.info("哈希获取缓存成功：键 = {}, 字段 = {}, 值 = {}", key, hashField, value);
                return value;
            } else {
                logger.info("哈希获取缓存失败：键 = {}, 字段 = {} 不存在或已过期", key, hashField);
                return null;
            }
        } catch (Exception e) {
            logger.error("哈希获取缓存异常：键 = {}, 字段 = {}", key, hashField, e);
            throw new CacheException("哈希获取缓存异常", e);
        }
    }

    /**
     * 批量获取缓存值
     * @param keys 要获取的键的集合
     * @return 包含存在的键值对的Map
     * @throws CacheException 如果批量获取缓存时发生异常
     */
    @NaslLogic
    public Map<String, String> getMultiCache(List<String> keys) {
        checkCacheInitialized("批量获取缓存");
        
        if (keys == null || keys.isEmpty()) {
            logger.warn("批量获取缓存失败: 键列表为空");
            return Collections.emptyMap();
        }
        
        try {
            Map<String, CacheDataWrapper> result = basicCommonCache.getAllPresent(keys);
            Map<String, String> returnMap = new LinkedHashMap<>(result.size());
            for (Map.Entry<String, CacheDataWrapper> entry : result.entrySet()) {
                returnMap.put(entry.getKey(), entry.getValue().getData());
            }
            logger.info("批量获取缓存成功: 获取到 {} 个键值对", returnMap.size());
            return returnMap;
        } catch (Exception e) {
            logger.error("批量获取缓存异常: {}", e.getMessage());
            throw new CacheException("批量获取缓存异常", e);
        }
    }

     /**
     * 获取缓存，如果缓存不存在则执行function，并将查询的值放到缓存中
     * @param key 缓存键
     * @param expireAfterWriteMillis 写入后过期时间（毫秒）
     * @param computeFunction 如果缓存不存在时，用于计算值的函数
     * @return 缓存值或计算后的新值
     * @throws CacheException 如果操作过程中发生异常
     */
    @NaslLogic
    public String getOrComputeAndSet(String key, Long expireAfterWriteMillis, Function<String, String> computeFunction) {
        checkCacheInitialized("获取或计算并设置缓存");

        if (StringUtils.isBlank(key)) {
            logger.warn("获取或计算并设置缓存失败: key（缓存键） 为空");
            return null;
        }

        if (expireAfterWriteMillis == null || expireAfterWriteMillis < 0) {
            logger.warn("获取或计算并设置缓存失败: expireAfterWriteMillis（写入后过期时间（毫秒）） 无效");
            return null;
        }

        if (computeFunction == null) {
            logger.warn("获取或计算并设置缓存失败: computeFunction（如果缓存不存在时，用于计算值的函数）为空");
            return null;
        }
        
        try {
            String value = getCache(key);
            if (value != null) {
                logger.info("从缓存中获取到值：键 = {}, 值 = {}", key, value);
                return value;
            }
            
            String computedValue = computeFunction.apply(key);
            if (computedValue != null) {
                boolean setSuccess = setCacheWithExpire(key, computedValue, expireAfterWriteMillis);
                if (setSuccess) {
                    logger.info("计算并设置缓存成功：键 = {}, 值 = {}, 过期时间 = {} 毫秒", key, computedValue, expireAfterWriteMillis);
                } else {
                    logger.warn("计算值成功但设置缓存失败：键 = {}, 值 = {}", key, computedValue);
                }
            } else {
                logger.info("计算值为null，不设置缓存：键 = {}", key);
            }
            
            return computedValue;
        } catch (Exception e) {
            logger.error("获取或计算并设置缓存异常：键 = {}", key, e);
            throw new CacheException("获取或计算并设置缓存异常", e);
        }
    }

    /**
     * 删除缓存
     * @param key 缓存键
     * @throws CacheException 如果删除缓存时发生异常
     */
    @NaslLogic
    public Boolean deleteCache(final String key) {
        checkCacheInitialized("删除缓存");
        
        if (StringUtils.isBlank(key)) {
            logger.warn("删除缓存失败: key为空或空白");
            return false;
        }
        
        try {
            basicCommonCache.invalidate(key);
            logger.info("删除缓存成功：键 = {}", key);
            return true;
        } catch (Exception e) {
            logger.error("删除缓存异常：键 = {}", key, e);
            return false;
        }
    }

    /**
     * 删除哈希缓存
     * @param key 缓存键
     * @param hashField 哈希字段
     * @throws CacheException 如果删除哈希缓存时发生异常
     */
    @NaslLogic
    public Boolean deleteHashCache(final String key, final String hashField) {
        checkCacheInitialized("删除哈希缓存");
        
        if (StringUtils.isBlank(key) || StringUtils.isBlank(hashField)) {
            logger.warn("删除哈希缓存失败: key或hashField为空或空白");
            return false;
        }
        
        try {
            basicCommonCache.invalidate(key + SEPARATOR + hashField);
            logger.info("删除哈希缓存成功：键 = {}, 字段 = {}", key, hashField);
            return true;
        } catch (Exception e) {
            logger.error("删除哈希缓存异常：键 = {}, 字段 = {}", key, hashField, e);
            return false;
        }
    }

    /**
     * 清空所有缓存
     * @throws CacheException 如果清空缓存时发生异常
     */
    @NaslLogic
    public Boolean clearAllCache() {
        checkCacheInitialized("清空缓存");
        
        try {
            basicCommonCache.invalidateAll();
            logger.info("清空所有缓存成功");
            return true;
        } catch (Exception e) {
            logger.error("清空所有缓存异常", e);
            return false;
        }
    }

     /**
     * 获取缓存的估计大小
     * @return 缓存中的估计条目数，如果缓存管理器未初始化则返回null
     */
    @NaslLogic
    public Long getEstimatedSize() {
        checkCacheInitialized("获取缓存估计大小");
        
        long size = basicCommonCache.estimatedSize();
        logger.info("当前缓存估计大小: {}", size);
        return size;
    }

    /* 检查缓存是否初始化
    * @param operation 操作名称
    * @throws CacheException 如果缓存管理器未初始化
    */
   private void checkCacheInitialized(String operation) {
       if (basicCommonCache == null) {
           throw new CacheException(operation + "失败: 缓存管理器未初始化");
       }
   }
}