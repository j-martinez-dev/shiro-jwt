package com.github.panchitoboy.shiro.jwt.realm;

import com.github.panchitoboy.shiro.jwt.repository.UserRepository;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.ops4j.pax.shiro.cdi.ShiroIni;

@ShiroIni
public class FormRealm extends AuthenticatingRealm {

    @Inject
    private UserRepository userRepository;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        AuthenticationToken user = userRepository.findByUserId(upToken.getUsername());
        if (user != null) {
            return new SimpleAccount(user, user.getCredentials(), getName());
        }

        return null;
    }
}
