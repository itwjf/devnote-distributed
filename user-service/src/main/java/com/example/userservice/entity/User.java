package com.example.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_USER";

    private String avatar = "/images/default-avatar.png";

    @Column(length = 500)
    private String bio = "这个人很神秘，什么也没写~";

    // 移除了与Post的关联，因为现在在不同服务中
    // 隐私设置字段保持不变
    private boolean showFollowers = true;
    private boolean showFollowing = true;
    private boolean showLikes = false;
    private boolean showFavorites = false;
}
