package com.github.panchitoboy.shiro.jwt.example.boundary;

import com.github.panchitoboy.shiro.jwt.example.entity.UserDefaultExample;
import com.github.panchitoboy.shiro.jwt.repository.UserDefault;
import com.github.panchitoboy.shiro.jwt.repository.UserRepository;
import org.apache.shiro.authc.credential.PasswordService;
import org.ops4j.pax.shiro.cdi.ShiroIni;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class UserRepositoryExample implements UserRepository {

    @Inject
    @ShiroIni
    PasswordService passwordService;

    private Map<Object, UserDefaultExample> userIdValues = new HashMap<>();
    private final Map<Object, UserDefaultExample> idValues = new HashMap<>();

    @PostConstruct
    public void init() {

        UserDefaultExample udt1 = new UserDefaultExample("id1", "userId1", passwordService.encryptPassword("password1"));
        udt1.addRole("service1");


        UserDefaultExample udt2 = new UserDefaultExample("id2", "userId2", passwordService.encryptPassword("password2"));


        userIdValues.put("userId1", udt1);
        userIdValues.put("userId2", udt2);

        idValues.put("id1", udt1);
        idValues.put("id2", udt2);
    }

    @Override
    public UserDefault findByUserId(Object userId) {
        return userIdValues.get(userId);
    }



}
