package io.jmartinez.shiro.jwt.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.jmartinez.shiro.jwt.repository.UserDefault;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDefaultExample implements UserDefault {

    public UserDefaultExample() {
    }

    public UserDefaultExample(String id, String userId, String password) {
        this.id = id;
        this.userId = userId;
        this.password = password;
    }

    private String id;
    private String userId;
    private String password;

    @Override
    @JsonIgnore
    public Object getPrincipal() {
        return getId();
    }

    @Override
    @JsonIgnore
    public Object getCredentials() {
        return getPassword();
    }

    @Override
    public String toString() {
        return this.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
