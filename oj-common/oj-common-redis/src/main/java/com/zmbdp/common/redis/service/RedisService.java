package com.zmbdp.common.redis.service;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //************************ Key 操作 ***************************

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return true表示存在，false表示不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置键的过期时间（默认单位为秒）
     *
     * @param key 键
     * @param timeout 过期时间
     * @return true表示设置成功，false表示设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置键的过期时间
     *
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return true表示设置成功，false表示设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        // return redisTemplate.expire(key, timeout, unit);
        // 不使用上面这个了，假如 redis 连接异常啊内部错误这些会返回 null，那这个就处理不了，
        // 下面这个方法如果说返回 false 和 null 都是 false，更加安全
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * 查看 key 的过期时间
     * @param key 键
     * @param unit 指定返回的时间单位，时分秒这种
     * @return 剩余的过期时间
     */
    public Long getExpire(final String key, final TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 删除单个对象
     *
     * @param key 键
     * @return true表示删除成功，false表示删除失败
     */
    public boolean deleteObject(final String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    //************************ String 操作 ***************************

    /**
     * 缓存基本对象，如Integer、String等
     *
     * @param key 缓存的键
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本对象并设置过期时间
     *
     * @param key 缓存的键
     * @param value 缓存的值
     * @param timeout 过期时间
     * @param timeUnit 时间单位
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 获取缓存的基本对象
     *
     * @param key 缓存的键
     * @param clazz 对象类型
     * @return 缓存的数据
     */
    public <T> T getCacheObject(final String key, Class<T> clazz) {
        ValueOperations<String, T> operation = (ValueOperations<String, T>) redisTemplate.opsForValue();
        T value = operation.get(key);
        return (value instanceof String) ? value : JSON.parseObject(String.valueOf(value), clazz);
    }

    /**
     * 计数器加一操作
     *
     * @param key redis 的 key
     * @return 目前的是多大
     */
    public Long increment(final String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    //************************ List 操作 ***************************

    /**
     * 获取列表长度
     *
     * @param key 列表键
     * @return 列表长度
     */
    public Long getListSize(final String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取列表中指定范围的数据
     *
     * @param key 列表键
     * @param start 起始位置
     * @param end 结束位置
     * @param clazz 对象类型
     * @return 列表数据
     */
    public <T> List<T> getCacheListByRange(final String key, long start, long end, Class<T> clazz) {
        List<?> range = redisTemplate.opsForList().range(key, start, end);
        return CollectionUtils.isEmpty(range) ? null : JSON.parseArray(JSON.toJSONString(range), clazz);
    }

    /**
     * 右侧批量插入数据到列表
     *
     * @param key 列表键
     * @param list 数据集合
     * @return 插入后的列表长度
     */
    public <T> Long rightPushAll(final String key, Collection<T> list) {
        return redisTemplate.opsForList().rightPushAll(key, list);
    }

    /**
     * 左侧插入单个数据到列表
     *
     * @param key 列表键
     * @param value 数据值
     * @return 插入后的列表长度
     */
    public <T> Long leftPushForList(final String key, T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从列表中删除指定数据
     *
     * @param key 列表键
     * @param value 数据值
     * @return 删除的元素数量
     */
    public <T> Long removeForList(final String key, T value) {
        return redisTemplate.opsForList().remove(key, 1L, value);
    }

    //************************ Hash 操作 ***************************

    /**
     * 获取哈希表中指定字段的值
     *
     * @param key 哈希键
     * @param hKey 字段键
     * @param clazz 对象类型
     * @return 字段对应的值
     */
    public <T> T getCacheMapValue(final String key, final String hKey, Class<T> clazz) {
        Object value = redisTemplate.opsForHash().get(key, hKey);
        return value != null ? JSON.parseObject(String.valueOf(value), clazz) : null;
    }

    /**
     * 获取哈希表中多个字段的值
     *
     * @param key 哈希键
     * @param hKeys 字段键集合
     * @param clazz 对象类型
     * @return 字段对应的值列表
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<String> hKeys, Class<T> clazz) {
        List<Object> list = redisTemplate.opsForHash().multiGet(key, new ArrayList<>(hKeys));
        List<T> result = new ArrayList<>();
        for (Object item : list) {
            result.add(JSON.parseObject(JSON.toJSONString(item), clazz));
        }
        return result;
    }

    /**
     * 设置哈希表中的字段值
     *
     * @param key 哈希键
     * @param hKey 字段键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 缓存整个哈希表
     *
     * @param key 哈希键
     * @param dataMap 数据
     */
    public <K, T> void setCacheMap(final String key, final Map<K, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 删除哈希表中的字段
     *
     * @param key 哈希键
     * @param hKey 字段键
     * @return 删除的字段数量
     */
    public Long deleteCacheMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey);
    }
}
