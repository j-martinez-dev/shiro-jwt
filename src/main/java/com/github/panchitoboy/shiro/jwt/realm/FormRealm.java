package com.github.panchitoboy.shiro.jwt.realm;

import com.github.panchitoboy.shiro.jwt.repository.UserDefault;
import com.github.panchitoboy.shiro.jwt.repository.UserRepository;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.ops4j.pax.shiro.cdi.ShiroIni;

@ShiroIni
public class FormRealm extends AuthorizingRealm {

    @Inject
    private UserRepository userRepository;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        UserDefault user = userRepository.findByUserId(upToken.getUsername());
        if (user != null) {
            SimpleAccount account = new SimpleAccount(user, user.getCredentials(), getName());
            account.addRole(user.getRoles());
            return account;
        }

        return null;
    }
    
        @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo(((UserDefault) principals.getPrimaryPrincipal()).getRoles());
    }
}
