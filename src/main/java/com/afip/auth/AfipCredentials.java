package com.afip.auth;

public class AfipCredentials {
    private final String token;
    private final String sign;
    
    public AfipCredentials(String token, String sign) {
        this.token = token;
        this.sign = sign;
    }
    
    public String getToken() {
        return token;
    }
    
    public String getSign() {
        return sign;
    }
}