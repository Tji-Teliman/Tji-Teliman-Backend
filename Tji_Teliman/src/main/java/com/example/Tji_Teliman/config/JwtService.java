package com.example.Tji_Teliman.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    private final Key key;
    private final long expirationMs;

    public JwtService(
        @Value("${app.jwt.secret:changeme-secret-key-please}") String secret,
        @Value("${app.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        // For simplicity, derive a key from secret; in prod use a proper Base64 key
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String telephone, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("tel", telephone)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Long parseUserId(String token) {
        try {
            var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            String sub = claims.getSubject();
            return sub == null ? null : Long.valueOf(sub);
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public String parseUserRole(String token) {
        try {
            var claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.get("role", String.class);
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }
}


