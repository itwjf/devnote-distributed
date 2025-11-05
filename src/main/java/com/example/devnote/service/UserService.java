package com.example.devnote.service;

import com.example.devnote.entity.Post;
import com.example.devnote.entity.User;
import com.example.devnote.repository.PostRepository;
import com.example.devnote.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    private UserRepository userRepository;


    private PostRepository postRepository;

    public UserService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    /**
     * 根据用户名查询用户信息
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 查询用户发表的所有文章（按时间倒序）
     */
    public List<Post> findPostsByUser(User user) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(user);
    }

    /**
     * 更新用户隐私设置
     * @param username 用户名
     * @param showFollowers 是否公开粉丝列表
     * @param showFollowing 是否公开关注列表
     * @param showLikes 是否公开点赞列表
     * @param showFavorites 是否公开收藏列表
     */
    public void updatePrivacySettings(String username,
                                      boolean showFollowers,
                                      boolean showFollowing,
                                      boolean showLikes,
                                      boolean showFavorites) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setShowFollowers(showFollowers);
            user.setShowFollowing(showFollowing);
            user.setShowLikes(showLikes);
            user.setShowFavorites(showFavorites);
            userRepository.save(user);
        }
    }
}
