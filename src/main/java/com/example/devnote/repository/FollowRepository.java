package com.example.devnote.repository;

import com.example.devnote.entity.Follow;
import com.example.devnote.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    // 检查是否已关注
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 获取某个用户的所有粉丝
    List<Follow> findByFollowing(User user);

    // 获取某个用户关注的所有人
    List<Follow> findByFollower(User user);

    // 取消关注
    void deleteByFollowerAndFollowing(User follower, User following);

    // 统计
    long countByFollower(User follower);
    long countByFollowing(User following);



}
