package com.phat.cinebox.config;

import com.nimbusds.jose.JOSEException;
import com.phat.cinebox.dto.TokenPayload;
import com.phat.cinebox.model.User;
import com.phat.cinebox.service.AuthenticationService;
import com.phat.cinebox.service.JwtService;
import com.phat.cinebox.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Trích xuất token từ cookies
        String accessToken = extractTokenFormCookies(request, "access_token");
        String refreshToken = extractTokenFormCookies(request, "refresh_token");

        // 2. Xác thực token từ cookies
        boolean authenticated = false;
        try {
            if (accessToken != null) {
                if (jwtService.validateAccessToken(accessToken)) {
                    setUpAuthentication(request, accessToken);
                    authenticated = true;
                }
            }
            if (refreshToken != null && !authenticated) {
                TokenPayload newAccessToken = jwtService.refreshToken(refreshToken, response);
                if (newAccessToken != null) {
                    setUpAuthentication(request, newAccessToken.getToken());
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        // Cho phép request đi tiếp vào Controller
        filterChain.doFilter(request, response);
    }

    private String extractTokenFormCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setUpAuthentication(HttpServletRequest request, String accessToken) {
        // 3. Nếu OK, lấy thông tin User, lưu vào Security Context để xác nhận request đã được xác thực thành công
        String username = null;
        try {
            username = jwtService.extractUsername(accessToken);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        User user = userService.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));

        List<SimpleGrantedAuthority> authorities = null;

        try {
            authorities = jwtService.extractRoles(accessToken);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, authorities);

        // Đây là dòng quan trọng nhất để thay thế cơ chế tự động của Spring:
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
