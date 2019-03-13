package com.wechat.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * Created by Administrator on 2018/3/19.
 */
public class RedisUtils {

    private static StringRedisTemplate stringRedisTemplate = SpringContextHolder.getBean("stringRedisTemplate");

    private static RedisTemplate<String, Object> redisTemplate = SpringContextHolder.getBean("redisTemplate");

    @Autowired(required = false)
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }

    /**
     *
     * @param key
     * @param value
     * @param timeout
     */
    public static void set(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     *
     * @param key
     * @param value
     * @param timeout
     */
    public static void set(String key, Object value, long timeout) {
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value instanceof Integer || value instanceof Long) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value instanceof Double || value instanceof Float) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else if (value instanceof Short || value instanceof Boolean) {
            stringRedisTemplate.opsForValue().set(key, value.toString());
        } else {
            redisTemplate.opsForValue().set(key,value);
        }
        if (timeout > 0) {
            expire(key, timeout);
        }
    }

    public static boolean setnx(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    public static void del(String key) {
        redisTemplate.delete(key);
    }

    public static void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    public static boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public static String get(String key) {
        return stringRedisTemplate.boundValueOps(key).get();
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> clazz) {
        if (clazz.equals(String.class)) {
            return (T) stringRedisTemplate.boundValueOps(key).get();
        } else if (clazz.equals(Integer.class) || clazz.equals(Long.class)) {
            return (T) stringRedisTemplate.boundValueOps(key).get();
        } else if (clazz.equals(Double.class) || clazz.equals(Float.class)) {
            return (T) stringRedisTemplate.boundValueOps(key).get();
        } else if (clazz.equals(Short.class) || clazz.equals(Boolean.class)) {
            return (T) stringRedisTemplate.boundValueOps(key).get();
        }
        return (T) redisTemplate.boundValueOps(key).get();
    }

    /**
     * 递增操作
     *
     * @param key
     * @param by
     * @return
     */
    public static double incr(String key, double by) {
        return stringRedisTemplate.opsForValue().increment(key, by);
    }

    /**
     * 递减操作
     *
     * @param key
     * @param by
     * @return
     */
    public static double decr(String key, double by) {
        return stringRedisTemplate.opsForValue().increment(key, -by);
    }

    /**
     * 递增操作
     *
     * @param key
     * @param by
     * @return
     */
    public static long incr(String key, long by) {
        return stringRedisTemplate.opsForValue().increment(key, by);
    }

    /**
     * 递减操作
     *
     * @param key
     * @param by
     * @return
     */
    public static long decr(String key, long by) {
        return stringRedisTemplate.opsForValue().increment(key, -by);
    }
}
