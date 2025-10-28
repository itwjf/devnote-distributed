package com.example.devnote.controller;

import com.example.devnote.entity.Post;
import com.example.devnote.entity.User;
import com.example.devnote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户主页：显示个人信息与文章列表
     */
    @GetMapping("/user/{username}")
    public String userProfile(@PathVariable String username,
                              Authentication authentication,
                              Model model) {
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "error";
        }

        List<Post> posts = userService.findPostsByUser(user);

        // 当前登录用户是否是本人
        boolean isSelf = authentication != null &&
                authentication.isAuthenticated() &&
                authentication.getName().equals(username);

        model.addAttribute("profileUser", user);
        model.addAttribute("posts", posts);
        model.addAttribute("isSelf", isSelf);

        return "user_profile";
    }

}
