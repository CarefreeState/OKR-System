package com.macaku.common.redis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
/**
* Created With Intellij IDEA
*   Description: 
*   User: 马拉圈
*   Date: 2024-01-19
*   Time: 23:19
*/
@SpringBootTest
class RedisCacheTest {

    @Resource
    private RedisCache redisCache;

    @Test
    void setCacheObject() {
        System.out.println(redisCache.getCacheObject("keykeykey"));
    }
}