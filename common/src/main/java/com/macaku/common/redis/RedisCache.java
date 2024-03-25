package com.macaku.common.redis;

import com.macaku.common.redis.component.RedisBloomFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings(value = { "unchecked", "rawtypes" })
public class RedisCache {

    private final RedisTemplate redisTemplate;

    private final RedisBloomFilter redisBloomFilter;

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public Boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
        log.info("为 Redis 的键值设置超时时间\t[{}]-[{}  {}]", key, timeout, timeUnit.name());
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    public <T> void execute(Runnable runnable) {
        log.info("Redis 执行原子任务");
        redisTemplate.multi();
        runnable.run();
        redisTemplate.exec();
    }

    /**
     * 获得对象的剩余存活时间
     *
     * @param key 键
     * @return 剩余存活时间
     */
    public long getKeyTTL(final String key) {
        int ttl = Math.toIntExact(redisTemplate.opsForValue().getOperations().getExpire(key));
        String message = null;
        switch (ttl) {
            case -1:
                message = "没有设置过期时间";
                break;
            case -2:
                message = "key不存在";
                break;
            default:
                message = ttl + "  " + TimeUnit.SECONDS.name();
                break;
        }
        log.info("查询 Redis key[{}] 剩余存活时间:{}", key, message);
        return TimeUnit.SECONDS.convert(ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        log.info("存入 Redis\t[{}]-[{}]", key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key    缓存的键值
     * @param value  缓存的值
     * @param timout 超时时间
     */
    public <T> void setCacheObject(final String key, final T value, final long timout, final TimeUnit timeUnit) {
        log.info("存入 Redis\t[{}]-[{}]，超时时间:[{}  {}]", key, value, timout, timeUnit.name());
        redisTemplate.opsForValue().set(key, value, timout, timeUnit);
    }

    /**
     * 获取键值
     *
     * @param key 键
     * @param <T>
     * @return 键对应的值，并封装成 Optional 对象
     */
    public <T> Optional<T> getCacheObject(final String key) {
        T value = (T) redisTemplate.opsForValue().get(key);
        log.info("查询 Redis\t[{}]-[{}]", key, value);
        return Optional.ofNullable(value);
    }

    /**
     * 让指定 Redis 键值进行自减
     *
     * @param key 键
     * @return 自减后的值
     */
    public long decrementCacheNumber(final String key) {
        long number = redisTemplate.opsForValue().decrement(key);
        log.info("Redis key[{}] 自减后：{}", key, number);
        return number;
    }

    /**
     * 让指定 Redis 键值进行自增
     *
     * @param key 键
     * @return 自增后的值
     */
    public long incrementCacheNumber(final String key) {
        long number = redisTemplate.opsForValue().increment(key);
        log.info("Redis key[{}] 自增后：{}", key, number);
        return number;
    }

    /**
     * 初始化布隆过滤器
     *
     * @param bloomFilterName
     */
    public void initBloomFilter(final String bloomFilterName) {
        log.info("初始化布隆过滤器[{}]", bloomFilterName);
        execute(() -> {
            redisBloomFilter.init(bloomFilterName);
        });
    }

    /**
     * 初始化布隆过滤器
     *
     * @param bloomFilterName
     * @param timeout
     * @param timeUnit
     */
    public void initBloomFilter(final String bloomFilterName, final long timeout, final TimeUnit timeUnit) {
        log.info("初始化布隆过滤器[{}]  超时时间:[{}  {}]", bloomFilterName, timeout, timeUnit);
        execute(() -> {
            redisBloomFilter.init(bloomFilterName);
            expire(bloomFilterName, timeout, timeUnit);
        });
    }

    /**
     * 加入布隆过滤器
     *
     * @param bloomFilterName 隆过滤器的名字
     * @param key             key 键
     */
    public <T> void addToBloomFilter(final String bloomFilterName, final T key) {
        log.info("加入布隆过滤器[{}]\tkey[{}]", bloomFilterName, key);
        execute(() -> {
            redisBloomFilter.add(bloomFilterName, key);
        });
    }

    /**
     * 布隆过滤器是否存在该键值
     *
     * @param bloomFilterName 布隆过滤器的名字
     * @param key             键
     * @return 键是否存在
     */
    public <T> boolean containsInBloomFilter(final String bloomFilterName, final T key) {
        boolean flag = redisBloomFilter.contains(bloomFilterName, key);
        log.info("key[{}]\t是否存在于布隆过滤器[{}]:\t{}", key, bloomFilterName, flag);
        return flag;
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param data
     */
    public <K, T> void setCacheMap(final String key, final Map<K, T> data) {
        if (Objects.nonNull(data)) {
            log.info("Map 存入 Redis\t[{}]-[{}]", key, data);
            redisTemplate.opsForHash().putAll(key, data);
        }
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param data
     */
    public <K, T> void setCacheMap(final String key, final Map<K, T> data, long timeout, final TimeUnit timeUnit) {
        if (Objects.nonNull(data)) {
            Map<String, T> map = new HashMap<>();
            data.entrySet().stream().parallel().forEach(entry -> {
                map.put(entry.getKey().toString(), entry.getValue());
            });
            log.info("尝试存入 Redis\t[{}]-[{}] 超时时间:[{}  {}]", key, map, timeout, timeUnit.name());
            execute(() -> {
                redisTemplate.opsForHash().putAll(key, map);
                expire(key, timeout, timeUnit);
            });
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <K, T> Optional<Map<K, T>> getCacheMap(final String key) {
        Map<K, T> data = redisTemplate.opsForHash().entries(key);
        data = data.size() == 0 ? null : data;
        log.info("获取 Redis 中的 Map 缓存\t[{}]-[{}]", key, data);
        return Optional.ofNullable(data);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key     Redis键
     * @param hashKey Hash键
     * @param value   值
     */
    public <K, T> void setCacheMapValue(final String key, final K hashKey, final T value) {
        log.info("存入 Redis 的某个 Map\t[{}.{}]-[{}]", key, hashKey, value);
        redisTemplate.opsForHash().put(key, hashKey.toString(), value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key     Redis键
     * @param hashKey Hash键
     * @return Hash中的对象
     */
    public <K, T> Optional<T> getCacheMapValue(final String key, final K hashKey) {
        T value = (T) redisTemplate.opsForHash().get(key, hashKey.toString());
        log.info("获取 Redis 中的 Map 的键值\t[{}.{}]-[{}]", key, hashKey, value);
        return Optional.ofNullable(value);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hashKey
     */
    public <K> void delCacheMapValue(final String key, final K hashKey) {
        log.info("删除 Redis 中的 Map 的键值\tkey[{}.{}]", key, hashKey);
        redisTemplate.opsForHash().delete(key, hashKey.toString());
    }

    /**
     * 让指定 HashMap 的键值进行自减
     *
     * @param key     HashMap的名字
     * @param hashKey HashMap的一个键
     * @return 自减后的值
     */
    public <K> long decrementCacheMapNumber(final String key, final K hashKey) {
        long number = redisTemplate.opsForHash().increment(key, hashKey.toString(), -1);
        log.info("Redis key[{}.{}] 自减后：{}", key, hashKey, number);
        return number;
    }

    /**
     * 让指定 HashMap 的键值进行自增
     *
     * @param key     HashMap的名字
     * @param hashKey HashMap的一个键
     * @return 自增后的值
     */
    public <K> long incrementCacheMapNumber(final String key, final K hashKey) {
        long number = redisTemplate.opsForHash().increment(key, hashKey.toString(), +1);
        log.info("Redis key[{}.{}] 自增后：{}", key, hashKey, number);
        return number;
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        log.info("删除 Redis 的键值\tkey[{}]", key);
        return redisTemplate.delete(key);
    }

    /**
     * 获得缓存的基本对象列表
     * *：匹配任意数量个字符（包括 0 个字符）
     *
     * @param prefix 字符串前缀
     * @return 键的集合
     */
    public Set<String> getCacheKeysByPrefix(final String prefix) {
        log.info("获取 Redis 以 [{}] 为前缀的键", prefix);
        return redisTemplate.keys(prefix + "*");
    }

    /**
     * 获得缓存的基本对象列表
     * *：匹配任意数量个字符（包括 0 个字符）
     * ?：匹配单个字符
     * []：匹配指定范围内的字符
     *
     * @param pattern 字符串格式
     * @return 键的集合
     */
    public Set<String> getCacheKeysByPattern(final String pattern) {
        log.info("获取 Redis 格式为 [{}] 的键", pattern);
        return redisTemplate.keys(pattern);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> void setCacheSet(final String key, final Set<T> dataSet) {
        log.info("存入 Redis 中的 Set 缓存\t[{}]-[{}]", key, dataSet);
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        dataSet.forEach(setOperation::add);
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        Set members = redisTemplate.opsForSet().members(key);
        log.info("获取 Redis 中的 Set 缓存\t[{}]-[{}]", key, members);
        return members;
    }


    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> void setCacheList(final String key, final List<T> dataList) {
        log.info("存入 Redis 中的 List 缓存\t[{}]-[{}]", key, dataList);
        redisTemplate.opsForList().rightPushAll(key, dataList);
    }

    /**
     * 缓存List数据（覆盖）
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> void setOverCacheList(final String key, final List<T> dataList) {
        log.info("存入 Redis 中的 List 缓存（覆盖）\t[{}]-[{}]", key, dataList);
        execute(() -> {
            deleteObject(key);
            redisTemplate.opsForList().rightPushAll(key, dataList);
        });
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        List<T> dataList = redisTemplate.opsForList().range(key, 0, -1);
        log.info("获取 Redis 中的 List 缓存\t[{}]-[{}]", key, dataList);
        return dataList;
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        List<T> dataList = redisTemplate.opsForHash().multiGet(key, hKeys);
        log.info("获取 Redis 中的 Map Value 缓存\t[{}.[{}]]-[{}]", key, hKeys, dataList);
        return dataList;
    }

}