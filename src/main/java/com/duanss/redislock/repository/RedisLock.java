package com.duanss.redislock.repository;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Repository;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @program: redis-lock
 * @description:
 * @author: 段闪闪 duanss
 * @create: 2019-10-22 13:29
 **/
@Repository
public class RedisLock {

    /**
     * 解锁脚本，原子操作
     */
    private static final String unlockScript =
            "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n"
                    + "then\n"
                    + "    return redis.call(\"del\",KEYS[1])\n"
                    + "else\n"
                    + "    return 0\n"
                    + "end";

    private StringRedisTemplate redisTemplate;

    public RedisLock(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** 
    * @Description: 加锁,有阻塞 
    * @Param: [name, expire, timeout] 
    * @return: java.lang.String 
    * @Author: 段闪闪 duanss 
    * @Date: 2019/10/22 13:30
    */ 
    public String lock(String name, long expire, long timeout){
        long startTime = System.currentTimeMillis();
        String token;
        do{
            token = tryLock(name, expire);
            if(token == null) {
                if((System.currentTimeMillis()-startTime) > (timeout-50))
                    break;
                try {
                    Thread.sleep(50); //try 50 per sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }while(token==null);

        return token;
    }

    /** 
    * @Description: 加锁,无阻塞 
    * @Param: [name, expire] 
    * @return: java.lang.String 
    * @Author: 段闪闪 duanss 
    * @Date: 2019/10/22 13:31
    */ 
    public String tryLock(String name, long expire) {
        String token = UUID.randomUUID().toString();
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        RedisConnection conn = factory.getConnection();
        try{
            Boolean result = conn.set(name.getBytes(StandardCharsets.UTF_8), token.getBytes(StandardCharsets.UTF_8),
                    Expiration.from(expire, TimeUnit.MILLISECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT);
            if(result!=null && result) {
                return token;
            }
        }finally {
            RedisConnectionUtils.releaseConnection(conn, factory,false);
        }
        return null;
    }

    /**
     * 解锁
     * @param name
     * @param token
     * @return
     */
    public boolean unlock(String name, String token) {
        byte[][] keysAndArgs = new byte[2][];
        keysAndArgs[0] = name.getBytes(StandardCharsets.UTF_8);
        keysAndArgs[1] = token.getBytes(StandardCharsets.UTF_8);
        RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
        assert factory != null;
        RedisConnection conn = factory.getConnection();
        try {
            Long result = conn.scriptingCommands().eval(unlockScript.getBytes(StandardCharsets.UTF_8), ReturnType.INTEGER, 1, keysAndArgs);
            if(result!=null && result>0) {
                return true;
            }
        }finally {
            RedisConnectionUtils.releaseConnection(conn, factory,false);
        }

        return false;
    }

}
