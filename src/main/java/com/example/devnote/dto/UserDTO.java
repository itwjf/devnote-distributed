package com.example.devnote.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {
    // 标记接口，表示该类可以被序列化
    // 为什么需要？Redis存储对象时需要序列化为字节流

    private Long id;
    private String username;
    private String avatar;
    private String bio;
    private boolean showFollowers;
    private boolean showFollowing;
}
