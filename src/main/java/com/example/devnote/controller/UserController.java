package com.example.devnote.controller;

import com.example.devnote.entity.Post;
import com.example.devnote.entity.User;
import com.example.devnote.repository.UserRepository;
import com.example.devnote.service.FollowService;
import com.example.devnote.service.PostService;
import com.example.devnote.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {


    private final UserService userService;


    private final UserRepository userRepository;


    private final FollowService followService;

    private final PostService postService;

    public UserController(UserService userService, UserRepository userRepository, FollowService followService, PostService postService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.followService = followService;
        this.postService = postService;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;// 相对路径，例如 "uploads/avatar/"

    @Value("${file.absolute-path:}") // 可选项，服务器使用
    private String absolutePath;

    /**
     * 用户主页：显示个人信息与文章列表
     */
    @GetMapping("/user/{username}")
    public String userProfile(@PathVariable String username,
                              Authentication authentication,
                              Model model) {
        //查询用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "error";
        }

        //查询用户文章
        List<Post> posts = userService.findPostsByUser(user);
        List<Post> likedPosts = postService.getLikedPosts(username);
        List<Post> favoritedPosts = postService.getFavoritedPosts(username);

        // 判断当前登录用户是否是本人
        boolean isSelf = authentication != null &&
                authentication.isAuthenticated() &&
                authentication.getName().equals(username);

        boolean isFollowing = false;

        //如果当前用户已登录，判断是否已关注
        if (!isSelf && authentication != null && authentication.isAuthenticated()){
            String currentUsername = authentication.getName();
            isFollowing = followService.isFollowing(currentUsername,username);
        }

        //统计粉丝数与关注数
        long followersCount = followService.countFollowers(username);
        long followingCount = followService.countFollowing(username);



        model.addAttribute("profileUser", user);
        model.addAttribute("posts", posts);
        model.addAttribute("isSelf", isSelf);
        model.addAttribute("isFollowing",isFollowing);
        model.addAttribute("followersCount", followersCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("likedPosts", likedPosts);
        model.addAttribute("favoritedPosts", favoritedPosts);

        return "user_profile";
    }

    /**
     * 进入个人资料编辑页面
     * @param username
     * @param authentication
     * @param model
     * @return
     */
    @GetMapping("/user/{username}/edit")
    public String editProfile(@PathVariable String username,Authentication authentication,Model model){

        if (authentication == null || !authentication.isAuthenticated()){
            return "redirect:/login";
        }

        // 仅允许本人编辑
        if (!authentication.getName().equals(username)) {
            return "redirect:/user/" + username + "?error=forbidden";
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            return "redirect:/error";
        }

        model.addAttribute("user", user);
        return "user_edit_profile";  // 进入编辑页
    }

    @PostMapping("/user/{username}/edit")
    public String updateProfile(
            @PathVariable String username,
            @RequestParam(value = "bio",required = false) String bio,
            @RequestParam(value = "avatar",required = false) MultipartFile avatarFile,
            Model model
    ) {
        User user = userRepository.findByUsername(username);

        user.setBio(bio);

        try {
            // 如果用户上传了头像
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String filename = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();

                // 确保目录存在
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                // 保存文件到本地
                File dest = new File(dir, filename);
                avatarFile.transferTo(dest);

                // 在数据库中保存相对路径（方便 Thymeleaf 显示）
                user.setAvatar("/uploads/" + filename);
            }

            userRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "头像上传失败，请重试！");
            return "user_edit_profile";
        }
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);

        return "redirect:/user/" + encodedUsername;
    }

    // UserController.java

    @GetMapping("/user/{username}/followers")
    public String viewFollowersPage(
            @PathVariable String username,
            Authentication authentication,
            Model model,
            @PageableDefault(size = 20) Pageable pageable) {

        User targetUser = userService.findByUsername(username);

        if (targetUser == null) {
            model.addAttribute("errorCode", "404");
            model.addAttribute("errorMessage", "用户不存在");
            return "error";
        }

        User currentUser = getLoggedInUser(authentication);
        boolean isSelf = currentUser != null && currentUser.getId().equals(targetUser.getId());

        // 🔒 权限检查：非本人 且 粉丝列表未公开 → 拒绝访问
        if (!isSelf && !targetUser.isShowFollowers()) {
            model.addAttribute("errorCode", "403");
            model.addAttribute("errorMessage", "该用户未公开粉丝列表");
            return "error";
        }

        // ✅ 通过权限检查后，才加载数据
        Page<User> followersPage = followService.getFollowersPage(username, currentUser, pageable);

        model.addAttribute("profileUser", targetUser);
        model.addAttribute("followersPage", followersPage);
        model.addAttribute("isSelf", isSelf);
        return "user_followers";
    }

    @GetMapping("/user/{username}/following")
    public String viewFollowingPage(
            @PathVariable String username,
            Authentication authentication,
            Model model,
            @PageableDefault(size = 20) Pageable pageable) {

        User targetUser = userService.findByUsername(username);

        if (targetUser == null) {
            model.addAttribute("errorCode", "404");
            model.addAttribute("errorMessage", "用户不存在");
            return "error";
        }

        User currentUser = getLoggedInUser(authentication);
        boolean isSelf = currentUser != null && currentUser.getId().equals(targetUser.getId());

        // 🔒 权限检查：非本人 且 未公开 → 拒绝访问
        if (!isSelf && !targetUser.isShowFollowing()) {
            model.addAttribute("errorCode", "403");
            model.addAttribute("errorMessage", "该用户未公开关注列表");
            return "error";
        }

        // ✅ 加载数据
        Page<User> followingPage = followService.getFollowingPage(username, currentUser, pageable);

        model.addAttribute("profileUser", targetUser);
        model.addAttribute("followingPage", followingPage);
        model.addAttribute("isSelf", isSelf);
        return "user_following";
    }

    // 工具方法：从 Authentication 获取当前用户
    private User getLoggedInUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return userService.findByUsername(authentication.getName());
        }
        return null;
    }



}
