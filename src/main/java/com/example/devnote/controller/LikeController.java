package com.example.devnote.controller;


import com.example.devnote.service.LikeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}")
    public Map<String, Object> toggleLike(@PathVariable Long postId, Authentication authentication) {
        String username = authentication.getName();
        // 调用 Service 层执行点赞/取消点赞逻辑（数据库更新）
        likeService.toggleLike(username, postId);

        long likeCount = likeService.countLikes(postId);
        boolean isLiked = likeService.isLikedByUser(username, postId);

        // 构造返回 JSON 数据
        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        response.put("likeCount", likeCount);

        // 返回 JSON 响应给前端（前端 AJAX 会根据这个更新页面）
        return response;
    }

}
