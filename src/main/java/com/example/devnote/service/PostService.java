package com.example.devnote.service;

import com.example.devnote.entity.Post;
import com.example.devnote.entity.User;
import com.example.devnote.repository.FavoriteRepository;
import com.example.devnote.repository.LikeRepository;
import com.example.devnote.repository.PostRepository;
import com.example.devnote.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final LikeRepository likeRepository;

    private final FavoriteRepository favoriteRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository, FavoriteRepository favoriteRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.favoriteRepository = favoriteRepository;
    }

    /**
     * 获取点赞文章列表
     * @param username
     * @return
     */
    public List<Post> getLikedPosts(String username) {
        User user = userRepository.findByUsername(username);
        return likeRepository.findByUser(user).stream()
                .map(like -> like.getPost())
                .collect(Collectors.toList());
    }

    /**
     * 获取收藏文章列表
     * @param username
     * @return
     */
    public List<Post> getFavoritedPosts(String username) {
        User user = userRepository.findByUsername(username);
        return favoriteRepository.findByUser(user).stream()
                .map(favorite -> favorite.getPost())
                .collect(Collectors.toList());
    }


}

