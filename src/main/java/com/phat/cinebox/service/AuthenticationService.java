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

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
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
        // Set Refresh Token vào cookies trình duyệt
        setRefreshTokenCookie(httpServletResponse, refreshPayload);
        // Trả về Access Token cho client
        return LoginResponse.builder()
                .accessToken(accessPayload.getToken())
                .build();
    }

    public void logout(String token, String refreshToken, HttpServletResponse response) throws ParseException {
        // Lấy các thông tin cần thiết
        JwtInfo jwtInfo = jwtService.parseToken(token);
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

        // Xóa cookies chứa refresh token
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, TokenPayload refreshPayload) {
        Cookie cookie = new Cookie("refresh_token", refreshPayload.getToken());

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(14)); //Refresh token sống 14 ngày

        response.addCookie(cookie);
    }

    public LoginResponse refreshToken(String refreshToken, HttpServletResponse httpServletResponse) throws ParseException, JOSEException {
        // Kiểm tra refresh token có hợp lệ không vì là API public
        if (!jwtService.validateRefreshToken(refreshToken)) {
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
            TokenPayload refreshPayload = this.jwtService.generateRefreshToken(user);
            setRefreshTokenCookie(httpServletResponse, refreshPayload);
            this.redisValidRefreshTokenRepository.deleteById(jwtId);
            // Lưu Refresh Token vào Redis
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

        return LoginResponse.builder()
                .accessToken(newAccessPayload.getToken())
                .build();
    }
}
