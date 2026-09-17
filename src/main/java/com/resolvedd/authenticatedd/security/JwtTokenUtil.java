package com.resolvedd.authenticatedd.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenUtil {

    private static final long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(1);

    private final SecretKey key = Keys.hmacShaKeyFor(
        "your-256-bit-secret-key-must-be-at-least-32-bytes-long!".getBytes(StandardCharsets.UTF_8)
    );

    public String createToken(String userId) {
        return Jwts.builder()
                .subject(userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}