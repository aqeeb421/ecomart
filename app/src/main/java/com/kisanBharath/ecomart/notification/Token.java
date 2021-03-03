package com.kisanBharath.ecomart.notification;

public class Token {
    private Object token;

    public Token(Object token) {
        this.token = token;
    }

    public Token() {
    }

    public Object getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
