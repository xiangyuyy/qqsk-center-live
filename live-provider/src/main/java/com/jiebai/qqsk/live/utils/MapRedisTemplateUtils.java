package com.jiebai.qqsk.live.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author xiaoh
 * @description: MapRedisTemplate 转化
 * @date 2020/2/2618:08
 */
public class MapRedisTemplateUtils {
    public static void setMapRedisTemplate(RedisTemplate redisTemplate){
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());//必须json 不然报错stting cant cast map
    }
}
