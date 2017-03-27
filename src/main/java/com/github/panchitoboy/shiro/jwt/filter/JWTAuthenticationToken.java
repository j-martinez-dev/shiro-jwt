package com.github.panchitoboy.shiro.jwt.filter;


import com.nimbusds.jwt.SignedJWT;
import org.apache.shiro.authc.AuthenticationToken;

public class JWTAuthenticationToken implements AuthenticationToken {

    private Object userId;
    private SignedJWT token;

    public JWTAuthenticationToken(Object userId, SignedJWT token) {
        this.userId = userId;
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return getUserId();
    }

    @Override
    public Object getCredentials() {
        return getToken();
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public SignedJWT getToken() {
        return token;
    }

    public void setToken(SignedJWT token) {
        this.token = token;
    }

}
