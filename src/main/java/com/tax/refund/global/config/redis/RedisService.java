package com.tax.refund.global.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate redisTemplate;

    public RedisService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // Redis에 Map으로 저장
    public void setMultiValues(Map<String, String> params){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.multiSet(params);
    }

    // Redis에 데이터 저장
    public void setValues(String key, String value){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value);
    }

    // Redis에 데이터 저장 소멸 시간 설정
    public void setValues(String key, String value, int second){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, value, Duration.ofSeconds(second)); // second 시간 뒤 데이터 삭제
    }
    
    // Redis에서 데이터 로드
    public String getValues(String key){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void delValues(String key) {
        redisTemplate.delete(key);
    }
}