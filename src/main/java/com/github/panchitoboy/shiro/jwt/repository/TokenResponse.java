package com.github.panchitoboy.shiro.jwt.repository;

public class TokenResponse {

    public TokenResponse() {
    }

    public TokenResponse(UserDefault user, String token) {
        this.user = user;
        this.token = token;
    }

    private String token;

    private UserDefault user;

    public String getToken() {
        return token;
    }

    public UserDefault getUser() {
        return user;
    }

}
