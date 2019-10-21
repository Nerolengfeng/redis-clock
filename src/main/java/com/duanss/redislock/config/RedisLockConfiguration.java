package com.duanss.redislock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @program: redis-lock
 * @description: redis锁
 * @author: 段闪闪 duanss
 * @create: 2019-10-21 13:39
 **/
@Configuration
public class RedisLockConfiguration {

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(
                redisConnectionFactory, "spring-cloud");
    }

}
