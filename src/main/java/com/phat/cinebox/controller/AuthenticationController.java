package com.phat.cinebox.controller;

import com.nimbusds.jose.JOSEException;
import com.phat.cinebox.dto.request.LoginRequest;
import com.phat.cinebox.dto.response.LoginResponse;
import com.phat.cinebox.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        return this.authenticationService.login(loginRequest, httpServletResponse);
    }

    @PostMapping("/auth/logout")
    void logout(@RequestHeader("Authorization") String authHeader, @CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse httpServletResponse) throws ParseException {
        String token =  authHeader.replace("Bearer ", "");
        authenticationService.logout(token, refreshToken, httpServletResponse);
    }

    @PostMapping("/auth/refresh")
    public LoginResponse refreshToken(@CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse httpServletResponse) throws ParseException, JOSEException {
        return this.authenticationService.refreshToken(refreshToken, httpServletResponse);
    }
}
