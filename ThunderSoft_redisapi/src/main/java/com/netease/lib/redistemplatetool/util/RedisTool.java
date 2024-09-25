package com.netease.lib.redistemplatetool.util;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
public class RedisTool {

    @Autowired
    public RedisTemplate<String, String> redisTemplate;

    /**
     * 设置 Redis 中指定 key 的值为指定字符串
     *
     * @param key   Redis 中的键
     * @param value Redis 中的值
     */
    @NaslLogic
    public String setValueTimeOut(String key, String value, Long timeout) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value, timeout, TimeUnit.SECONDS);
        return ops.get(key);
    }

    /**
     * 设置 Redis 中指定 key 的值为指定字符串
     *
     * @param key   Redis 中的键
     * @param value Redis 中的值
     */
    @NaslLogic
    public String setValue(String key, String value) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value);
        return ops.get(key);
    }

    @NaslLogic
    public List<String> getListValue(String key) {
        ListOperations<String, String> ops = redisTemplate.opsForList();
        return ops.range(key, 0, -1);
    }

    @NaslLogic
    public List<String> setListValue(String key, List<String> value) {
        ListOperations<String, String> ops = redisTemplate.opsForList();
        ops.rightPushAll(key, value);
        return ops.range(key, 0, -1);
    }

    /**
     * 获取 Redis 中指定 key 的值
     *
     * @param key Redis 中的键
     * @return Redis 中的值
     */
    @NaslLogic
    public String getValue(String key) throws NullPointerException {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }

    /**
     * 如果 Redis 中不存在指定 key，则设置为指定字符串
     *
     * @param key   Redis 中的键
     * @param value Redis 中的值
     * @return 如果设置成功，返回 true，否则返回 false
     */
    @NaslLogic
    public Boolean setIfAbsent(String key, String value) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.setIfAbsent(key, value);
    }

    /**
     * 如果 Redis 中不存在指定 key，则设置为指定字符串，并设置过期时间
     *
     * @param key     Redis 中的键
     * @param value   Redis 中的值
     * @param timeout 过期时间
     * @return 如果设置成功，返回 true，否则返回 false
     */
    @NaslLogic
    public Boolean setIfAbsentWithExpire(String key, String value, Long timeout) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 如果 Redis 中存在指定 key，则设置为指定字符串，并设置过期时间
     *
     * @param key     Redis 中的键
     * @param value   Redis 中的值
     * @param timeout 过期时间
     * @return 如果设置成功，返回 true，否则返回 false
     */
    @NaslLogic
    public Boolean setIfPresentWithExpire(String key, String value, Long timeout) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.setIfPresent(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 将 Redis 中指定 key 的值增加指定的整数 delta
     *
     * @param key   Redis 中的键
     * @param delta 增加的整数值
     * @return 增加后的结果值
     */
    @NaslLogic
    public Long incrementLong(String key, Long delta) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.increment(key, delta);
    }

    /**
     * 将 Redis 中指定 key 的值增加指定的浮点数 delta
     *
     * @param key   Redis 中的键
     * @param delta 增加的浮点数值
     * @return 增加后的结果值
     */
    @NaslLogic
    public Double incrementDoulbe(String key, Double delta) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.increment(key, delta);
    }

    /**
     * 删除 Redis 中指定的 key
     *
     * @param key Redis 中的键
     */
    @NaslLogic
    public Boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }

    public String convertToJsonString(String input) {
        return input.replace("\\", "");
    }

    /**
     * 将多个键值对设置到 Redis 哈希表中，并返回更新后的哈希表。
     *
     * @param hashKey     哈希表的键
     * @param keyValueMap 包含要设置到 Redis 哈希表中的键值对的 Map
     * @return 表示更新后 Redis 哈希表的 Map
     */
    @NaslLogic
    public Map<String, String> setHashValues(String hashKey, Map<String, String> keyValueMap) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            ops.put(hashKey, entry.getKey(), entry.getValue());
        }
        return ops.entries(hashKey);
    }

    /**
     * 获取 Redis 哈希表中指定 key 的所有域和值
     *
     * @param hashKey 哈希表的 key
     * @return 包含所有域和值的 Map 对象，键为域，值为对应的值
     */
    @NaslLogic
    public Map<String, String> getHashValuesWithNoKey(String hashKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.entries(hashKey);
    }

    /**
     * 从 Redis 哈希表中获取多个键对应的值，并返回一个包含这些键值对的 Map。
     *
     * @param hashKey   哈希表的键
     * @param fieldKeys 包含要获取值的键的列表
     * @return 一个包含指定键值对的 Map
     */
    @NaslLogic
    public Map<String, String> getHashValues(String hashKey, List<String> fieldKeys) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        List<String> values = ops.multiGet(hashKey, fieldKeys);

        Map<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < fieldKeys.size(); i++) {
            String key = fieldKeys.get(i);
            String value = values.get(i);
            if (value != null) {
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }

    /**
     * 根据传入的 key 和 field 列表查询 Redis 中对应的值，并将查询结果存储在一个 Map<String, Map<String, String>> 中
     *
     * @param keyAndFieldsMap Map<String, String>，其中 key 是 Redis 中的 key，value 是要查询的 field
     * @return Map<String, Map < String, String>>，其中外层的 key 是 Redis 中的 key，内层的 key-value 对是 field 和它对应的值
     */
    @NaslLogic
    public Map<String, Map<String, String>> getHashValuesByKeysAndField(Map<String, String> keyAndFieldsMap) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Map<String, Map<String, String>> resultMap = new HashMap<>();

        for (Map.Entry<String, String> entry : keyAndFieldsMap.entrySet()) {
            String hashKey = entry.getKey();
            String fieldKey = entry.getValue();
            String value = ops.get(hashKey, fieldKey);
            if (value != null) {
                Map<String, String> fieldMap = new HashMap<>();
                fieldMap.put(fieldKey, value);
                resultMap.put(hashKey, fieldMap);
            }
        }

        return resultMap;
    }

    /**
     * 根据传入的 key 和 field 列表查询 Redis 中对应的值，并将查询结果存储在一个 Map<String, Map<String, String>> 中
     *
     * @param keyAndFieldsMap Map<String, List<String>>，其中 key 是 Redis 中的 key，value 是要查询的 hashMap 中对应的 field 的列表
     * @return Map<String, Map < String, String>>，其中外层的 key 是 Redis 中的 key，内层的 key-value 对是 field 和它对应的值
     */
    @NaslLogic
    public Map<String, Map<String, String>> getHashValuesByKeysAndFiledList(Map<String, List<String>> keyAndFieldsMap) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Map<String, Map<String, String>> resultMap = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : keyAndFieldsMap.entrySet()) {
            String hashKey = entry.getKey();
            List<String> fieldKeys = entry.getValue();

            List<String> values = ops.multiGet(hashKey, fieldKeys);

            Map<String, String> fieldMap = new HashMap<>();
            for (int i = 0; i < fieldKeys.size(); i++) {
                String key = fieldKeys.get(i);
                String value = values.get(i);
                if (value != null) {
                    fieldMap.put(key, value);
                }
            }

            resultMap.put(hashKey, fieldMap);
        }

        return resultMap;
    }

    /**
     * 删除 Redis 哈希表中指定的一个或多个域
     *
     * @param hashKey 哈希表的 key
     * @param keys    要删除的一个或多个域
     * @return 被删除的域的数量
     */
    @NaslLogic
    public Long deleteHashfieldKeys(String hashKey, List<String> keys) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        String[] fieldKeysArray = keys.toArray(new String[0]);
        return ops.delete(hashKey, fieldKeysArray);
    }

    /**
     * 检查 Redis 哈希表中是否存在指定的域
     *
     * @param hashKey  哈希表的 key
     * @param fieldKey 要检查的域
     * @return 如果存在，返回 true；否则，返回 false
     */
    @NaslLogic
    public Boolean hashfieldKeyExists(String hashKey, String fieldKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.hasKey(hashKey, fieldKey);
    }

    /**
     * 将指定的域设置为指定的值，仅当域不存在时才进行设置
     *
     * @param hashKey  哈希表的 key
     * @param fieldKey 要设置的域
     * @param value    要设置的值
     * @return 如果设置成功，返回 true；如果域已存在，不进行设置，返回 false
     */
    @NaslLogic
    public Boolean hashSetIfNotExists(String hashKey, String fieldKey, String value) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.putIfAbsent(hashKey, fieldKey, value);
    }

    /**
     * 获取 Redis 哈希表中域的数量
     *
     * @param hashKey 哈希表的 key
     * @return 哈希表中域的数量
     */
    @NaslLogic
    public Long hashSize(String hashKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.size(hashKey);
    }

    /**
     * 获取 Redis 哈希表中的所有域
     *
     * @param hashKey 哈希表的 key
     * @return 哈希表中的所有域
     */
    @NaslLogic
    public List<String> hashKeys(String hashKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return new ArrayList<>(ops.keys(hashKey));
    }

    /**
     * 将 Redis 哈希表中指定域的值增加指定的增量
     *
     * @param hashKey   哈希表的 key
     * @param fieldKey  要增加值的域
     * @param increment 增量
     * @return 增加指定增量后域的值
     */
    @NaslLogic
    public Long hashIncrementBy(String hashKey, String fieldKey, Long increment) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.increment(hashKey, fieldKey, increment);
    }

    /**
     * 将 Redis 哈希表中指定域的值增加指定的浮点数增量
     *
     * @param hashKey   哈希表的 key
     * @param fieldKey  要增加值的域
     * @param increment 增量
     * @return 增加指定增量后域的值
     */
    @NaslLogic
    public Double hashIncrementByFloat(String hashKey, String fieldKey, Double increment) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.increment(hashKey, fieldKey, increment);
    }
}
