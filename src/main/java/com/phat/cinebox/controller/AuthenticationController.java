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
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse, @RequestParam(value = "continue", required = false) String continueUrl) {
        try {
            this.authenticationService.login(loginRequest, httpServletResponse);
            // 1. Kiểm tra an toàn cho continueUrl
            String targetUrl = "/"; // Mặc định về home
            if (continueUrl != null && !continueUrl.isBlank() && continueUrl.startsWith("/") && !continueUrl.equals("error")) {
                targetUrl = continueUrl;
            }
            // 2. Trả về JSON chứa URL đích
            return ResponseEntity.ok(Map.of("redirectTo", targetUrl));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/auth/logout")
    void logout(@CookieValue(name="access_token") String accessToken, @CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse httpServletResponse) throws ParseException {
        authenticationService.logout(accessToken, refreshToken, httpServletResponse);
    }

    @PostMapping("/auth/refresh")
    public void refreshToken(@CookieValue(name = "refresh_token") String refreshToken, HttpServletResponse httpServletResponse) throws ParseException, JOSEException {
        this.authenticationService.refreshToken(refreshToken, httpServletResponse);
    }
}
