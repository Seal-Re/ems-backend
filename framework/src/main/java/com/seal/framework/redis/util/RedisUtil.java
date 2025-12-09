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

    // 删除值
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    // 设置过期时间
    public void setWithExpire(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }


    public void putHash(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHash(String key, String hashKey) {
        return stringRedisTemplate.opsForHash().get(key, hashKey);
    }


    /**
     * 添加消息到列表并限制列表长度
     * ✅ 重点：这里入参改为了 String，完美解决 "文件夹/乱码" 问题
     */
    public void addMessageWithLimit(String key, String message, int limit) {

        stringRedisTemplate.opsForList().leftPush(key, message);
        stringRedisTemplate.opsForList().trim(key, 0, limit - 1);
    }

    // 获取列表中的所有消息
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