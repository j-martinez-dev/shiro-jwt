package com.github.panchitoboy.shiro.jwt.realm;

import com.github.panchitoboy.shiro.jwt.JWTGeneratorVerifier;
import com.github.panchitoboy.shiro.jwt.filter.JWTAuthenticationToken;
import com.github.panchitoboy.shiro.jwt.repository.UserRepository;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.ops4j.pax.shiro.cdi.ShiroIni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Arrays;

@ShiroIni
public class JWTRealm extends AuthorizingRealm {

    Logger logger = LoggerFactory.getLogger(JWTRealm.class);

    @Inject
    private UserRepository userRepository;


    public void setJwtService(JWTGeneratorVerifier jwtGeneratorVerifier) {
        this.jwtService = jwtGeneratorVerifier;
    }

    @Inject
    private JWTGeneratorVerifier jwtService;



    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof JWTAuthenticationToken;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        JWTAuthenticationToken upToken = (JWTAuthenticationToken) token;

        if (jwtService.validateToken(upToken.getToken())) {
            SimpleAccount account = new SimpleAccount(upToken.getPrincipal(), upToken.getToken(), getName());
            String[] roles = new String[0];
            try {
                roles = upToken.getToken().getJWTClaimsSet().getStringArrayClaim("roles");
            } catch (ParseException e) {

                logger.error("ParseException parsing roles from JWT", e);
            }
            account.addRole(Arrays.asList(roles));
            return account;
        }

        return null;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        SimpleAccount info = null;

        if(isAuthenticationCachingEnabled())
        {
            Object key = getAuthenticationCacheKey(principals);

            if(key!=null)
            {
                info =  (SimpleAccount) getAuthenticationCache().get(key);
            }
            else {
                logger.error("Account not found in Authentication cache");
            }
        }
        else
        {
            logger.error("Authentication Cache not enabled. This needs to be enabled!");
        }

        return info;
    }




}
