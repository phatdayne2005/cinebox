package com.phat.cinebox.controller;

import com.phat.cinebox.dto.request.LoginRequest;
import com.phat.cinebox.dto.response.LoginResponse;
import com.phat.cinebox.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return this.authenticationService.login(loginRequest);
    }
}
