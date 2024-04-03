package com.macaku.redis.repository;

import com.macaku.common.exception.GlobalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-03
 * Time: 10:52
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLock {

    @Value("${spring.redisson.wait}")
    private Long wait; // 秒

    @Value("${spring.redisson.timeout}")
    private Long timeout; // 秒

    private final RedissonClient redisClient;

    public RLock getLock(final String key) {
        return redisClient.getLock(key);
    }

    public boolean tryLock(RLock rLock, final Long var1,
                           final Long var2, final TimeUnit timeUnit) throws InterruptedException {
        String key = rLock.getName();
        log.info("尝试获取锁 {}, wait {} {}, timeout {} {}", key, var1, timeUnit, var2, timeUnit);
        boolean lock = rLock.tryLock(var1, var2, timeUnit);
        if(Boolean.TRUE.equals(lock)) {
            log.info("尝试获取锁 {} 成功", key);
        } else {
            log.info("尝试获取锁 {} 失败", key);
        }
        return lock;
    }

    public void unlock(RLock rLock) {
        log.info("释放锁 {}", rLock.getName());
        rLock.unlock();
    }

    public void tryLockDoSomething(final String key, Runnable behavior1, Runnable behavior2) {
        // 获得锁实例
        RLock rLock = getLock(key);
        // 在 Redis 中尝试获取锁
        try {
            boolean lock = tryLock(rLock, wait, timeout, TimeUnit.SECONDS);
            if(Boolean.TRUE.equals(lock)) {
                behavior1.run();
            } else {
                behavior2.run();
            }
        } catch (InterruptedException e) {
            throw new GlobalServiceException(e.getMessage());
        } finally {
            unlock(rLock);
        }
    }

    public void tryLockDoSomething(final String key, final Long var1, final Long var2, final TimeUnit timeUnit,
                                   Runnable behavior1, Runnable behavior2) {
        // 获得锁实例
        RLock rLock = getLock(key);
        // 在 Redis 中尝试获取锁
        try {
            boolean lock = tryLock(rLock, var1, var2, timeUnit);
            if(Boolean.TRUE.equals(lock)) {
                behavior1.run();
            } else {
                behavior2.run();
            }
        } catch (InterruptedException e) {
            throw new GlobalServiceException(e.getMessage());
        } finally {
            unlock(rLock);
        }
    }

    public <T> T tryLockDoSomething(final String key, Supplier<T> supplier1, Supplier<T> supplier2) {
        // 获得锁实例
        RLock rLock = getLock(key);
        // 在 Redis 中尝试获取锁
        try {
            boolean lock = tryLock(rLock, wait, timeout, TimeUnit.SECONDS);
            if(Boolean.TRUE.equals(lock)) {
                return supplier1.get();
            } else {
                return supplier2.get();
            }
        } catch (InterruptedException e) {
            throw new GlobalServiceException(e.getMessage());
        } finally {
            unlock(rLock);
        }
    }

    public <T> T tryLockDoSomething(final String key, final Long var1, final Long var2, final TimeUnit timeUnit,
                                    Supplier<T> supplier1, Supplier<T> supplier2) {
        // 获得锁实例
        RLock rLock = getLock(key);
        // 在 Redis 中尝试获取锁
        try {
            boolean lock = tryLock(rLock, var1, var2, timeUnit);
            if(Boolean.TRUE.equals(lock)) {
                return supplier1.get();
            } else {
                return supplier2.get();
            }
        } catch (InterruptedException e) {
            throw new GlobalServiceException(e.getMessage());
        } finally {
            unlock(rLock);
        }
    }

}
