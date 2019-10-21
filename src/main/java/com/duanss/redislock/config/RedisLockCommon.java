package com.duanss.redislock.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @program: redis-lock
 * @description:
 * @author: 段闪闪 duanss
 * @create: 2019-10-21 15:11
 **/
@Component
public class RedisLockCommon {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //获取尝试时间
    private final long EXPIRE_TIME = 5 * 10000;

    /**
     * Redis加锁的操作
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean tryLock(String key, String value) {
        if (stringRedisTemplate.opsForValue().setIfAbsent(key, value, EXPIRE_TIME, TimeUnit.SECONDS)) {
            return true;
        }
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(currentValue) && Long.valueOf(currentValue) < System.currentTimeMillis()) {
            //获取上一个锁的时间 如果高并发的情况可能会出现已经被修改的问题  所以多一次判断保证线程的安全
            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Redis解锁的操作
     *
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        try {
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                stringRedisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
        }
    }

}
