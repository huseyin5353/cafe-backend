package com.restaurantbackend.restaurantbackend.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secretKey;

    @Value("${jwt.expiration:604800000}") // 7 gün (milisaniye)
    private Long jwtExpiration;

    /**
     * Session için JWT token oluşturur
     */
    public String createSessionToken(Long sessionId, String customerName, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sessionId", sessionId);
        claims.put("customerName", customerName);
        claims.put("deviceId", deviceId);
        claims.put("type", "session");
        
        return buildToken(claims, customerName, jwtExpiration);
    }

    /**
     * JWT token oluşturur
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token'dan session bilgilerini çıkarır
     */
    public SessionInfo getSessionInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Long sessionId = claims.get("sessionId", Long.class);
            String customerName = claims.get("customerName", String.class);
            String deviceId = claims.get("deviceId", String.class);
            return new SessionInfo(sessionId, customerName, deviceId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Token'ın geçerli olup olmadığını kontrol eder
     */
    public boolean isValidToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Token'ın süresi dolmuş mu kontrol eder
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Token'dan expiration date'i çıkarır
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Token'dan tüm claims'leri çıkarır
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Token'dan belirli bir claim'i çıkarır
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Token'dan customer name'i çıkarır
     */
    public String extractCustomerName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Token'dan session ID'yi çıkarır
     */
    public Long extractSessionId(String token) {
        return extractClaim(token, claims -> claims.get("sessionId", Long.class));
    }

    /**
     * Token'dan device ID'yi çıkarır
     */
    public String extractDeviceId(String token) {
        return extractClaim(token, claims -> claims.get("deviceId", String.class));
    }

    /**
     * Signing key'i oluşturur
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Session bilgilerini tutan inner class
     */
    public static class SessionInfo {
        private final Long sessionId;
        private final String customerName;
        private final String deviceId;

        public SessionInfo(Long sessionId, String customerName, String deviceId) {
            this.sessionId = sessionId;
            this.customerName = customerName;
            this.deviceId = deviceId;
        }

        public Long getSessionId() {
            return sessionId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public String getDeviceId() {
            return deviceId;
        }
    }
}