package com.phat.cinebox.service;

import com.phat.cinebox.dto.request.UserCreateRequest;
import com.phat.cinebox.dto.response.UserCreateResponse;
import com.phat.cinebox.model.User;
import com.phat.cinebox.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public UserCreateResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getUsername()))
            throw new RuntimeException("Username is already in use");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setEmail(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return UserCreateResponse.builder().email(user.getEmail()).build();
    }
}
