package com.example.zencash.utils;

import com.example.zencash.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${SECRET_KEY}") // Lấy giá trị từ application.properties hoặc .env
    private String secretKey;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30 phút
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;
    private String getSignKey() {
        return secretKey; // Trả về chuỗi secretKey trực tiếp
    }

    public String generateAccessToken(User user) {
        return generateAccessToken(user.getEmail(), user.getUsername());
    }

    // Overloaded method để dùng cho Refresh Token
    public String generateAccessToken(String email, String username) {
        return Jwts.builder()
                .setSubject(email) // Email là subject
                .claim("username", username) // Thêm username vào payload
                .setIssuedAt(new Date()) // Thời điểm tạo token
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION)) // Hạn token
                .signWith(SignatureAlgorithm.HS512, getSignKey())

                .compact();
    }


    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("username", user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION)) // 7 ngày
                .signWith(SignatureAlgorithm.HS512, getSignKey())

                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date()); // Trả về true nếu token đã hết hạn
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public Date getExpirationDate(String token) {
        return extractExpiration(token);
    }

}
