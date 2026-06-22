package com.vn.keycap_server.service.redis;

public interface IRedisService {

    <T> void set(String key, T value, long ttlMinutes);

    <T> T get(String key, Class<T> clazz);

    void evictByPattern(String pattern);

}
