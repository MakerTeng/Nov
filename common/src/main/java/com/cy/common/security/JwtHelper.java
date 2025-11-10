package com.cy.common.security;

import com.cy.common.exception.BizException;
import com.cy.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHelper {

    private final JwtProperties properties;

    public String generateToken(LoginUser loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", loginUser.getUsername());
        claims.put("role", loginUser.getRole());

        Instant now = Instant.now();
        Instant expireAt = now.plusSeconds(properties.getExpireMinutes() * 60);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(loginUser.getUserId()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expireAt))
                .setIssuer(properties.getIssuer())
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public LoginUser parseToken(String token) {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String userIdStr = body.getSubject();
            Long userId = Long.parseLong(userIdStr);
            String username = body.get("username", String.class);
            String role = body.get("role", String.class);
            return LoginUser.builder()
                    .userId(userId)
                    .username(username)
                    .role(role)
                    .build();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "token is invalid or expired");
        }
    }

    private Key signingKey() {
        byte[] keyBytes = ensureSecretLength(properties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] ensureSecretLength(String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "jwt secret not configured");
        }
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= 32) {
            return bytes;
        }
        byte[] padded = Arrays.copyOf(bytes, 32);
        return padded;
    }
}
