package com.afip.auth;

import java.time.LocalDateTime;

public class AfipCredentials {
    private final String token;
    private final String sign;
    private final LocalDateTime expiration;
    
    public AfipCredentials(String token, String sign, LocalDateTime expiration) {
        this.token = token;
        this.sign = sign;
        this.expiration = expiration;
    }
    
    public AfipCredentials(String token, String sign) {
        this(token, sign, LocalDateTime.now().plusHours(12)); // Default 12 horas
    }
    
    public String getToken() {
        return token;
    }
    
    public String getSign() {
        return sign;
    }
    
    public LocalDateTime getExpiration() {
        return expiration;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration.minusMinutes(5)); // 5 min buffer
    }
}