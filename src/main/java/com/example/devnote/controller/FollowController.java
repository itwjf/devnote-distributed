package com.example.devnote.controller;

import com.example.devnote.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private  final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    /**
     * 关注用户
     */
    @PostMapping("/{username}/follow")
    public String follow(@PathVariable String username,
                         @AuthenticationPrincipal UserDetails currentUser,
                         RedirectAttributes redirectAttributes){
        System.out.println("正在尝试关注用户：" + username + "，发起人：" + currentUser.getUsername());

        try {
            followService.follow(currentUser.getUsername(), username);
            System.out.println("✅ followService.follow() 已执行");

            redirectAttributes.addFlashAttribute("message", "已关注 " + username);
        } catch (RuntimeException e) {
            System.out.println("❌ followService.follow() 执行失败：" + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // 对 username 进行 UTF-8 URL 编码
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        return "redirect:/user/" + encodedUsername;
    }

    /**
     * 取消关注
     */
    @PostMapping("/{username}/unfollow")
    public String unfollow(@PathVariable String username,
                           @AuthenticationPrincipal UserDetails currentUser,
                           RedirectAttributes redirectAttributes) {
        try {
            followService.unfollow(currentUser.getUsername(), username);
            redirectAttributes.addFlashAttribute("message", "已取消关注 " + username);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);

        return "redirect:/user/" + encodedUsername;
    }

}
