package com.phat.cinebox.config;

import com.nimbusds.jose.JOSEException;
import com.phat.cinebox.model.User;
import com.phat.cinebox.service.JwtService;
import com.phat.cinebox.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        // 1. Trích xuất token từ header
        String token = extractToken(request);
        String path = request.getRequestURI();

        // 2. Xác thực token từ header
        try {
            if (token != null) {
                if (!path.equals("/auth/refresh") && jwtService.validateRefreshToken(token)) {
                    // Trả về lỗi 401
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Only access token can use this API");
                    return;
                }
                if (jwtService.validateAccessToken(token)) {

                    // 3. Nếu OK, lấy thông tin User, lưu vào Security Context để xác nhận request đã được xác thực thành công
                    String username = null;
                    try {
                        username = jwtService.extractUsername(token);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    User user = userService.findByEmail(username).orElseThrow(() -> new RuntimeException("User not found"));

                    List<SimpleGrantedAuthority> authorities = null;

                    try {
                        authorities = jwtService.extractRoles(token);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);

                    // Đây là dòng quan trọng nhất để thay thế cơ chế tự động của Spring:
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }

        // Cho phép request đi tiếp vào Controller
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
