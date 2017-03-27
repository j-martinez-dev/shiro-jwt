package com.github.panchitoboy.shiro.jwt.producer;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.ops4j.pax.shiro.cdi.ShiroIni;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class SecurityProducer {

    @Produces
    @ShiroIni
    @Named
    public PasswordService passwordService() {
        return new DefaultPasswordService();
    }


}
