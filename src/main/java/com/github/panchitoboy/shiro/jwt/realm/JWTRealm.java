package com.github.panchitoboy.shiro.jwt.realm;

import com.github.panchitoboy.shiro.jwt.filter.JWTAuthenticationToken;
import com.github.panchitoboy.shiro.jwt.repository.UserRepository;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.ops4j.pax.shiro.cdi.ShiroIni;

@ShiroIni
public class JWTRealm extends AuthenticatingRealm {

    @Inject
    private UserRepository userRepository;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof JWTAuthenticationToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        JWTAuthenticationToken upToken = (JWTAuthenticationToken) token;
        AuthenticationToken user = userRepository.findById(upToken.getUserId());

        if (user != null && userRepository.validateToken(upToken.getToken())) {
            return new SimpleAccount(user, upToken.getToken(), getName());
        }

        return null;
    }

}
