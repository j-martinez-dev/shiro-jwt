package com.github.panchitoboy.shiro.jwt.repository;

public interface UserRepository {

    UserDefault findByUserId(Object userId);

    UserDefault findById(Object id);

}
