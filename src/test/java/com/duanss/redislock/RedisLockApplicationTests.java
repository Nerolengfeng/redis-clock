package com.duanss.redislock;

import com.duanss.redislock.repository.RedisLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisLockApplicationTests {

    @Autowired
    RedisLock redisLock;

    /** 
    * @Description: 加锁测试 
    * @Param: [] 
    * @return: void 
    * @Author: 段闪闪 duanss 
    * @Date: 2019/10/22 14:00
    */ 
    @Test
    void contextLoads() {
        String dssTEST = redisLock.tryLock("dssTEST2", 20000L);
        System.out.println(dssTEST);

    }

    /** 
    * @Description: 解锁测试 
    * @Param: [] 
    * @return: void 
    * @Author: 段闪闪 duanss 
    * @Date: 2019/10/22 14:01
    */ 
    @Test
    void Test2() {
        boolean dssTEST2 = redisLock.unlock("dssTEST2", "67cb0a7d-672a-432c-abad-dfce0241f175");
        System.out.println(dssTEST2);
    }

}
