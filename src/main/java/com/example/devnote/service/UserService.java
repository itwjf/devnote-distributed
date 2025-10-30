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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;


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


}
