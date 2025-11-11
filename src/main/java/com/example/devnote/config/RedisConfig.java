package com.example.devnote.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * Redis配置类
 * 作用：配置RedisTemplate，定义key和value的序列化方式
 * 面试考点：为什么要自定义RedisTemplate？默认的有什么问题？
 */
@Configuration
public class RedisConfig {

    /**
     * 创建RedisTemplate Bean
     * RedisTemplate是Spring操作Redis的核心类
     *
     * @param connectionFactory Redis连接工厂，由Spring自动注入
     * @return 配置好的RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,Object> template=new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // key使用String序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // value使用JSON序列化
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//        GenericJackson2JsonRedisSerializer
// Jackson库的Redis序列化器，将对象转为JSON存储
// 优势：可读性好，支持复杂对象，跨语言兼容

        template.afterPropertiesSet();

        return template;
    }
}
