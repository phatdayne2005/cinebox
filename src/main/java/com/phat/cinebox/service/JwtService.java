package com.phat.cinebox.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.phat.cinebox.dto.JwtInfo;
import com.phat.cinebox.dto.TokenPayload;
import com.phat.cinebox.model.RedisInvalidAccessToken;
import com.phat.cinebox.model.RedisValidRefreshToken;
import com.phat.cinebox.model.User;
import com.phat.cinebox.repository.RedisInvalidAccessTokenRepository;
import com.phat.cinebox.repository.RedisValidRefreshTokenRepository;
import com.phat.cinebox.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret-key}")
    private String secretKey;
    private final RedisInvalidAccessTokenRepository redisInvalidAccessTokenRepository;
    private final RedisValidRefreshTokenRepository redisValidRefreshTokenRepository;
    private final UserRepository userRepository;

    public TokenPayload generateAccessToken(User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        Date issueTime = new Date();
        Date expirationTime = Date.from(issueTime.toInstant().plus(30, ChronoUnit.MINUTES));
        String jwtID = UUID.randomUUID().toString();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().
                subject(user.getUsername())
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .jwtID(jwtID)
                .claim("token_type", "ACCESS")
                .claim("scope", user.getRole().getName())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);

        try{
            jwsObject.sign(new MACSigner(secretKey));
        } catch(JOSEException e){
            throw new RuntimeException(e);
        }

        String token = jwsObject.serialize();
        return TokenPayload.builder()
                .token(token)
                .jwtId(jwtID)
                .expiredTime(expirationTime)
                .build();
    }

    public TokenPayload generateRefreshToken(User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        Date issueTime = new Date();
        Date expirationTime = Date.from(issueTime.toInstant().plus(14, ChronoUnit.DAYS));
        String jwtID = UUID.randomUUID().toString();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .jwtID(jwtID)
                .claim("token_type", "REFRESH")
                .claim("scope", user.getRole().getName())
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);

        try{
            jwsObject.sign(new MACSigner(secretKey));
        } catch(JOSEException e){
            throw new RuntimeException(e);
        }

        String token = jwsObject.serialize();
        return TokenPayload.builder()
                .token(token)
                .expiredTime(expirationTime)
                .jwtId(jwtID)
                .build();

    }

    public boolean verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime.before(new Date())) {
            return false;
        }
        return signedJWT.verify(new MACVerifier(secretKey));
    }

    public JwtInfo parseToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date issueTime = signedJWT.getJWTClaimsSet().getIssueTime();
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return JwtInfo.builder()
                .jwtId(jwtId)
                .issueTime(issueTime)
                .expirationTime(expirationTime)
                .build();
    }

    public String extractUsername(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }

    public List<SimpleGrantedAuthority> extractRoles(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        String roleName =  signedJWT.getJWTClaimsSet().getClaim("scope").toString();
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    public boolean validateAccessToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Optional<RedisInvalidAccessToken> accessTokenById = this.redisInvalidAccessTokenRepository.findById(signedJWT.getJWTClaimsSet().getJWTID());
        if (accessTokenById.isPresent()) {
            return false;
        }
        return verifyToken(token);
    }

    public boolean validateRefreshToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Boolean isValid = this.redisValidRefreshTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID());
        if (!isValid) {
            return false;
        }
        return verifyToken(token);
    }

}
