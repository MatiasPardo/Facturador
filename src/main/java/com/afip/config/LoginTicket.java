package com.afip.config;

public class LoginTicket {
    private final String token;
    private final String sign;

    public LoginTicket(String token, String sign) {
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
