package com.github.panchitoboy.shiro.jwt.filter;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;


import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Generated Shiro {@link AuthenticationToken} based on whether it is a Login or post login
 * For Login it will generate a {@link UsernamePasswordToken}
 * For post Login it will generate a {@link JWTAuthenticationToken}
 *
 * This also sets the Login URL in case a redirect is required when session expires etc.
 */
public final class JWTOrFormAuthenticationFilter extends AuthenticatingFilter {

    public static final String USER_ID = "userId";
    public static final String PASSWORD = "password";

    protected static final String AUTHORIZATION_HEADER = "Authorization";

    public JWTOrFormAuthenticationFilter() {
        setLoginUrl(DEFAULT_LOGIN_URL);
    }

    @Override
    public void setLoginUrl(String loginUrl) {
        String previous = getLoginUrl();
        if (previous != null) {
            this.appliedPaths.remove(previous);
        }
        super.setLoginUrl(loginUrl);
        this.appliedPaths.put(getLoginUrl(), null);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        boolean loggedIn = false;

        if (isLoginRequest(request, response) || isLoggedAttempt(request, response)) {
            loggedIn = executeLogin(request, response);
        }

        if (!loggedIn) {
            HttpServletResponse httpResponse = WebUtils.toHttp(response);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return loggedIn;
    }


    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws IOException {

        if (isLoginRequest(request, response)) {

            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            try
            {
                JSONObject json = (JSONObject) parser.parse(request.getInputStream());
                String username = (String)json.get(USER_ID);
                String password = (String)json.get(PASSWORD);
                return new UsernamePasswordToken(username, password);

            }catch (ParseException ex)
            {
                throw new IOException("Could not parse JSON", ex);
            }
        }

        if (isLoggedAttempt(request, response)) {
            String jwtToken = getAuthzHeader(request);
            if (jwtToken != null) {
                return createJWTToken(jwtToken);
            }
        }

        return new UsernamePasswordToken();
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        return false;
    }

    protected boolean isLoggedAttempt(ServletRequest request, ServletResponse response) {
        String authzHeader = getAuthzHeader(request);
        return authzHeader != null;
    }

    protected String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        return httpRequest.getHeader(AUTHORIZATION_HEADER);
    }

    /**
     * Create the Shiro {@link JWTAuthenticationToken} from the received JWT string
     *
     * @param  base64 encoded JWT Token
     * @return JWTAuthenticationToken
     * @throws AuthenticationException if JWT token cannot be parsed or has 'none' set as algorithm
     */
    public AuthenticationToken createJWTToken(String token) {
        try {
            SignedJWT jwsObject = SignedJWT.parse(token);
            if(jwsObject.getHeader().getAlgorithm().equals(JWSAlgorithm.NONE))
            {
                throw new AuthenticationException("JWT Token Algorithm cannot be set to 'none'");
            }
            return new JWTAuthenticationToken(jwsObject.getJWTClaimsSet().getSubject(), jwsObject);

        } catch (java.text.ParseException ex) {
            throw new AuthenticationException(ex);
        }

    }

}
