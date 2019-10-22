package com.duanss.redislock;

import com.duanss.redislock.repository.RedisLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class RedisLockApplicationTests {

    @Autowired
    RedisLock redisLock;

    @Test
    void contextLoads() {

        String dssTEST = redisLock.tryLock("dssTEST2", 20000L);
        System.out.println(dssTEST);

    }

    @Test
    void Test2() {
        boolean dssTEST2 = redisLock.unlock("dssTEST2", "67cb0a7d-672a-432c-abad-dfce0241f175");
        System.out.println(dssTEST2);
    }

}
