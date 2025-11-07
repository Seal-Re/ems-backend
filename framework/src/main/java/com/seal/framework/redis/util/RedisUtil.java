package com.seal.framework.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 设置值
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 获取值
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 删除值
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    // 设置值并指定过期时间
    public void setWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    // 哈希操作
    public void putHash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHash(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 添加消息到列表并限制列表长度
     * @param key 列表的键
     * @param message 要存储的消息
     * @param limit 列表的最大长度
     */
    public void addMessageWithLimit(String key, Object message, int limit) {
        redisTemplate.opsForList().leftPush(key, message);
        redisTemplate.opsForList().trim(key, 0, limit - 1);
    }

    // 获取列表中的所有消息
    public List<Object> getMessages(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    // 集合操作

    /**
     * 向集合中添加一个成员
     * @param key 集合的键
     * @param member 要添加的成员
     * @return 成功添加的成员数量
     */
    public Long addSetMember(String key, Object member) {
        return redisTemplate.opsForSet().add(key, member);
    }

    /**
     * 从集合中移除一个或多个成员
     * @param key 集合的键
     * @param member 要移除的成员
     * @return 成功移除的成员数量
     */
    public Long removeSetMember(String key, Object member) {
        return redisTemplate.opsForSet().remove(key, member);
    }

    /**
     * 获取集合中的所有成员
     * @param key 集合的键
     * @return 包含所有成员的Set
     */
    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

}