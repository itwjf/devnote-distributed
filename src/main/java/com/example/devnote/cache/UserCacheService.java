package com.example.devnote.cache;

import com.example.devnote.dto.UserDTO;
import com.example.devnote.entity.User;
import com.example.devnote.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
// Lombok注解，自动生成log字段：private static final Logger log = LoggerFactory.getLogger(UserCacheService.class);
// 作用：简化日志声明，避免重复代码
@RequiredArgsConstructor
public class UserCacheService {


    // Spring提供的Redis操作模板，封装了Redis命令
    // 为什么用泛型<String, Object>？
    // - String: key的类型，通常用String便于管理
    // - Object: value的类型，可以存储任意对象（通过序列化）
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    private static final String USER_CACHE_KEY = "user:";
    private static final Duration CACHE_TTL = Duration.ofHours(2);// Java 8时间API，表示2小时的时间段

    /**
     * 获取用户信息 - 带缓存
     * 面试考点：缓存读取流程
     */
    public UserDTO getUserById(Long userId) {
        String cacheKey = USER_CACHE_KEY + userId;

        try {
            // 1. 先查缓存
            UserDTO userDTO = (UserDTO) redisTemplate.opsForValue().get(cacheKey);
            if (userDTO != null) {
                log.info("从缓存获取用户: {}", userId);
                return userDTO;
            }

            // 2. 缓存未命中，查询数据库
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return null;
            }

            // 3. 转换为DTO并缓存
            userDTO = convertToDTO(user);
            redisTemplate.opsForValue().set(cacheKey, userDTO, CACHE_TTL);
            log.info("用户数据已缓存: {}", userId);

            return userDTO;

        } catch (Exception e) {
            log.error("获取用户缓存失败，直接查询数据库: {}", userId, e);
            User user = userRepository.findById(userId).orElse(null);
            return user != null ? convertToDTO(user) : null;
        }
    }

    /**
     * 更新用户缓存
     */
    public void updateUserCache(User user) {
        if (user == null || user.getId() == null) return;

        String cacheKey = USER_CACHE_KEY + user.getId();
        try {
            UserDTO userDTO = convertToDTO(user);
            redisTemplate.opsForValue().set(cacheKey, userDTO, CACHE_TTL);
            log.info("更新用户缓存: {}", user.getId());
        } catch (Exception e) {
            log.error("更新用户缓存失败: {}", user.getId(), e);
        }
    }

    /**
     * 删除用户缓存
     */
    public void evictUserCache(Long userId) {
        String cacheKey = USER_CACHE_KEY + userId;
        try {
            redisTemplate.delete(cacheKey);
            log.info("删除用户缓存: {}", userId);
        } catch (Exception e) {
            log.error("删除用户缓存失败: {}", userId, e);
        }
    }

    /**
     * Entity 转 DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setAvatar(user.getAvatar());
        dto.setBio(user.getBio());
        dto.setShowFollowers(user.isShowFollowers());
        dto.setShowFollowing(user.isShowFollowing());
        return dto;
    }
}
