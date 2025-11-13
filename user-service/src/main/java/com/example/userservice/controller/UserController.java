package com.example.userservice.controller;


import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息
     * 访问：http://localhost:8081/api/users/{username}
     */
    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String username) {
        log.info("获取用户信息: {}", username);

        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", user);
        response.put("message", "获取用户信息成功");

        return ResponseEntity.ok(response);
    }


    /**
     * 用户注册
     * 访问：POST http://localhost:8081/api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        log.info("用户注册: {}", user.getUsername());

        // 这里应该添加密码加密、验证等逻辑
        User savedUser = userService.saveUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", savedUser);
        response.put("message", "用户注册成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查接口
     * 访问：http://localhost:8081/api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "user-service");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

}
