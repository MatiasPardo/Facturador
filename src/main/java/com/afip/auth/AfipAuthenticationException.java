package com.afip.auth;

public class AfipAuthenticationException extends Exception {
    public AfipAuthenticationException(String message) {
        super(message);
    }
    
    public AfipAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}