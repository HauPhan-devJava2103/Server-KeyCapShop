package com.vn.keycap_server.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService implements IRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // Save Object Redis (JSON, TTL)
    @Override
    public <T> void set(String key, T value, long ttlMinutes) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttlMinutes, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            // Log warning, không throw – cache miss chỉ giảm performance
            log.warn("Failed to cache dashboard data for key: {}", key, e);
        }
    }

    // Chuyển đổi String - Java Object Redis
    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            return json != null ? objectMapper.readValue(json, clazz) : null;
        } catch (JsonProcessingException e) {
            log.warn("Redis cache GET failed for key: {}", key, e);
            return null;
        }
    }

    // Delete All Key Matching Pattern
    @Override
    public void evictByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
