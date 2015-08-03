package com.github.panchitoboy.shiro.jwt.repository;

import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationToken;

public interface UserDefault extends AuthenticationToken {

    default Set<String> getRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("default");
        return roles;
    }
}
