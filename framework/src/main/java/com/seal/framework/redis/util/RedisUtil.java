package com.seal.framework.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public void setWithExpire(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void putHash(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHash(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    public void addMessageWithLimit(String key, String message, int limit) {
        stringRedisTemplate.opsForList().leftPush(key, message);
        stringRedisTemplate.opsForList().trim(key, 0, limit - 1);
    }

    public List<String> getMessages(String key) {
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }

    public Long addSetMember(String key, String member) {
        return stringRedisTemplate.opsForSet().add(key, member);
    }

    public Long removeSetMember(String key, String member) {
        return stringRedisTemplate.opsForSet().remove(key, member);
    }

    public Set<String> getSetMembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return this.stringRedisTemplate;
    }
}
