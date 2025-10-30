package com.example.devnote.service;

import com.example.devnote.entity.Follow;
import com.example.devnote.entity.User;
import com.example.devnote.repository.FollowRepository;
import com.example.devnote.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;


    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    /**
     * 关注用户
     */
    public void follow(String followerUsername,String followingUsername){
        if (followerUsername.equals(followingUsername)){
            throw new RuntimeException("不能关注自己");
        }

        User follower = userRepository.findByUsername(followerUsername);
        User following = userRepository.findByUsername(followingUsername);


        if (follower ==  null || following == null){
            throw new RuntimeException("用户不存在");
        }

        // 检查是否已关注
        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            throw new RuntimeException("已经关注过该用户");
        }

        System.out.println("FollowService: 正在保存关注关系：" + followerUsername + " -> " + followingUsername);
        followRepository.save(new Follow(follower,following));
        System.out.println("✅ FollowRepository.save() 已执行");
    }

    /**
     * 取消关注
     */
    public void unfollow(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User following = userRepository.findByUsername(followingUsername);

        if (follower == null || following == null) {
            throw new RuntimeException("用户不存在");
        }

        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    /**
     * 判断是否已关注
     */
    public boolean isFollowing(String followerUsername, String followingUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User following = userRepository.findByUsername(followingUsername);
        if (follower == null || following == null) return false;
        return followRepository.findByFollowerAndFollowing(follower, following).isPresent();
    }

    /**
     * 获取粉丝列表
     */
    public List<Follow> getFollowers(String username) {
        User user = userRepository.findByUsername(username);
        return followRepository.findByFollowing(user);
    }


    /**
     * 获取关注列表
     */
    public List<Follow> getFollowing(String username) {
        User user = userRepository.findByUsername(username);
        return followRepository.findByFollower(user);
    }

    /**
     * 获取粉丝个数
     */
    public long countFollowers(String username) {
        User user = userRepository.findByUsername(username);
        return followRepository.countByFollowing(user);
    }

    /**
     * 获取关注个数
     */
    public long countFollowing(String username) {
        User user = userRepository.findByUsername(username);
        return followRepository.countByFollower(user);
    }
}
