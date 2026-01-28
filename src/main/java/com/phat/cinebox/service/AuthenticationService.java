package com.phat.cinebox.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.phat.cinebox.dto.JwtInfo;
import com.phat.cinebox.dto.TokenPayload;
import com.phat.cinebox.dto.request.LoginRequest;
import com.phat.cinebox.dto.response.LoginResponse;
import com.phat.cinebox.model.RedisInvalidAccessToken;
import com.phat.cinebox.model.RedisValidRefreshToken;
import com.phat.cinebox.model.User;
import com.phat.cinebox.repository.RedisInvalidAccessTokenRepository;
import com.phat.cinebox.repository.RedisValidRefreshTokenRepository;
import com.phat.cinebox.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisInvalidAccessTokenRepository redisInvalidAccessTokenRepository;
    private final RedisValidRefreshTokenRepository redisValidRefreshTokenRepository;
    private final UserService userService;

    public void login(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User user = (User) authentication.getPrincipal();
        TokenPayload accessPayload = this.jwtService.generateAccessToken(user);
        TokenPayload refreshPayload = this.jwtService.generateRefreshToken(user);
        long ttlInMilliseconds = refreshPayload.getExpiredTime().getTime() - System.currentTimeMillis();
        // Lưu Refresh Token vào Redis
        redisValidRefreshTokenRepository.save(RedisValidRefreshToken
                .builder()
                        .jwtId(refreshPayload.getJwtId())
                        .expirationTime(ttlInMilliseconds)
                .build()
        );
        // Set Access Token và Refresh Token vào cookies trình duyệt
        setAccessTokenCookie(httpServletResponse, accessPayload);
        // Set Refresh Token vào cookies trình duyệt
        setRefreshTokenCookie(httpServletResponse, refreshPayload);
    }

    public void logout(String accessToken, String refreshToken, HttpServletResponse response) throws ParseException {
        // Lấy các thông tin cần thiết
        JwtInfo jwtInfo = jwtService.parseToken(accessToken);
        String jwtId =  jwtInfo.getJwtId();
        Date issueTime = jwtInfo.getIssueTime();
        Date expirationTime = jwtInfo.getExpirationTime();

        // Kiểm tra token còn hạn sử dụng không
        if (expirationTime.before(new Date())) {
            return;
        }

        long ttlInMilliseconds = expirationTime.getTime() - System.currentTimeMillis();

        // Lưu Access Token đã đăng xuất vào Black list Redis
        RedisInvalidAccessToken redisInvalidAccessToken = RedisInvalidAccessToken.builder()
                .jwtId(jwtId)
                .expirationTime(ttlInMilliseconds)
                .build();

        redisInvalidAccessTokenRepository.save(redisInvalidAccessToken);

        // Xóa Refresh Token trong cookies nếu có
        if (refreshToken != null && !refreshToken.isEmpty()){
            jwtInfo = jwtService.parseToken(refreshToken);
            jwtId =  jwtInfo.getJwtId();
            if (redisValidRefreshTokenRepository.existsById(jwtId))
                redisValidRefreshTokenRepository.deleteById(jwtId);
        }

        // Xóa cookies chứa access token
        removeTokenCookie(response, "access_token");

        // Xóa cookies chứa refresh token
        removeTokenCookie(response, "refresh_token");
    }

    public void removeTokenCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public void setAccessTokenCookie(HttpServletResponse response, TokenPayload accessPayload) {
        Cookie accessCookies = new Cookie("access_token", accessPayload.getToken());

        accessCookies.setHttpOnly(true);
        accessCookies.setSecure(true);
        accessCookies.setPath("/");
        accessCookies.setMaxAge((int) TimeUnit.MINUTES.toSeconds(30)); // Access token sống 30 phút

        response.addCookie(accessCookies);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, TokenPayload refreshPayload) {
        Cookie refreshCookies = new Cookie("refresh_token", refreshPayload.getToken());

        refreshCookies.setHttpOnly(true);
        refreshCookies.setSecure(true);
        refreshCookies.setPath("/");
        refreshCookies.setMaxAge((int) TimeUnit.DAYS.toSeconds(14)); // Refresh token sống 14 ngày

        response.addCookie(refreshCookies);
    }

    public void refreshToken(String refreshToken, HttpServletResponse httpServletResponse) throws ParseException, JOSEException {
        // Kiểm tra refresh token có hợp lệ không vì là API public
        if (!jwtService.validateRefreshToken(refreshToken)) {
            removeTokenCookie(httpServletResponse, refreshToken);
            throw new RuntimeException("Refresh token is invalid");
        }

        // Lấy thông tin cần thiết
        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        String email = signedJWT.getJWTClaimsSet().getSubject();
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        long expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime().getTime();
        long sevenDaysInMiliseconds = TimeUnit.DAYS.toMillis(7);
        // Nếu Refresh token còn hạn sử dụng <= 7 ngày, cấp Refresh token mới
        if (expiredTime - System.currentTimeMillis() <=  sevenDaysInMiliseconds) {
            // Generate Refresh Token mới và lưu vào cookies
            TokenPayload refreshPayload = this.jwtService.generateRefreshToken(user);
            setRefreshTokenCookie(httpServletResponse, refreshPayload);
            // Xóa Refresh Token cũ
            this.redisValidRefreshTokenRepository.deleteById(jwtId);
            // Lưu Refresh Token mới vào Redis
            long ttlInMilliseconds = refreshPayload.getExpiredTime().getTime() - System.currentTimeMillis();
            redisValidRefreshTokenRepository.save(RedisValidRefreshToken
                    .builder()
                    .jwtId(refreshPayload.getJwtId())
                    .expirationTime(ttlInMilliseconds)
                    .build()
            );
        }
        // Cấp Access Token mới
        TokenPayload newAccessPayload = jwtService.generateAccessToken(user);

        // Set access token cookies mới
        setAccessTokenCookie(httpServletResponse, newAccessPayload);
    }
}
