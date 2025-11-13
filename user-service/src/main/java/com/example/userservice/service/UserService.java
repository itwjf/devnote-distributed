package com.example.userservice.service;


import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByUsername(String username) {
        log.info("查询用户: {}", username);
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        log.info("保存用户: {}", user.getUsername());
        return userRepository.save(user);
    }

    public User findById(Long id) {
        log.info("根据ID查询用户: {}", id);
        return userRepository.findById(id).orElse(null);
    }
}
