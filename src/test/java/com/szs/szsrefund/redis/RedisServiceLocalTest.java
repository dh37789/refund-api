package com.szs.szsrefund.redis;

import com.szs.szsrefund.global.config.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("local")
public class RedisServiceLocalTest {

    @Autowired
    private RedisService redisService;

    @Test
    @Order(1)
    @DisplayName("redis set, get 테스트")
    void redis_set_Test() {
        // given
        String key = "userId";
        String id = "abcd123";

        // when
        redisService.setValues(key, id);

        // then
        assertThat(redisService.getValues(key)).isEqualTo(id);
    }

    @Test
    @Order(2)
    @DisplayName("redis 5초 timeout 테스트")
    void redis_multiSet_Test() {
        // given
        Map<String, String> redisMap = new HashMap<>();
        redisMap.put("userId", "abcd123");
        redisMap.put("name", "홍길동");

        // when
        redisService.setMultiValues(redisMap);

        // then
        assertThatEqual(redisMap);
    }

    private void assertThatEqual(Map<String, String> redisMap) {
        assertThat(redisService.getValues("userId")).isEqualTo(redisMap.get("userId"));
        assertThat(redisService.getValues("name")).isEqualTo(redisMap.get("name"));
    }

    @Test
    @Order(3)
    @DisplayName("redis multiSet 테스트")
    void redis_timeout_Test() throws InterruptedException {
        // given
        String userId = "홍길동";

        // when
        redisService.setValues("userId", userId, 5);

        // then
        assertThat(redisService.getValues("userId")).isEqualTo(userId);
        Thread.sleep(6000);
        assertThat(redisService.getValues("userId")).isNull();
    }

}
