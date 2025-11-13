package com.example.devnote.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final RedisTemplate<String,Object> redisTemplate;

    /**
     * 测试Redis连接
     * @return
     */
    @GetMapping("/test/redis")
    public String testRedis() {
        try {
            log.info("开始测试Redis连接...");

            // 测试Redis写入
            redisTemplate.opsForValue().set("test-key", "Hello Redis!", Duration.ofMinutes(10));
            log.info("Redis写入成功");

            // 测试Redis读取
            String value = (String) redisTemplate.opsForValue().get("test-key");
            log.info("Redis读取成功: {}", value);

            return "Redis连接成功: " + value;

        } catch (Exception e) {
            log.error("Redis连接失败", e);
            return "Redis连接失败: " + e.getMessage();
        }
    }

}
