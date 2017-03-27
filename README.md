# shiro-jwt

Shiro-jwt is a library that allows to use apache shiro with JWT (Json Web Tokens).

### Dependencies:
- shiro-core: Shiro core module
- shiro-web: Shiro web module
- pax-shiro-cdi-web: Shiro CDI integration
- nimbus-jose-jwt: JWT library

## Getting Started:

We are gonna add some modifications to the tutorial: [Securing Web Applications with Apache Shiro](http://shiro.apache.org/webapp-tutorial.html) 

## Step 1: Enable Shiro

###1a: Add a shiro.ini file

This is a traditional shiro.ini file, the important information here is the use of the JWTOrFormAuthenticationFilter. This filter receive a property (loginUrl) that will be the endpoint to do the login with an user and password. All the others endpoints are gonna be validated againts a JWT.

It's important to say that there are two realms defined:
- FormRealm - Used to Authenticate the user and build the JWT Token with roles
- JWTRealm - Validates the incoming JWT Token and parses it for roles.

Modify the below <b>jwtService</b> properties for session expiration, issuer and the secret key to use (should be sufficiently random string see https://www.grc.com/passwords.htm)


 
<pre>
[main]
builtInCacheManager = org.apache.shiro.cache.MemoryConstrainedCacheManager

<b>securityManager.realms = $jWTRealm, $formRealm</b>
securityManager.subjectDAO.sessionStorageEvaluator.sessionStorageEnabled = false
securityManager.cacheManager = $builtInCacheManager

passwordMatcher = org.apache.shiro.authc.credential.PasswordMatcher
passwordMatcher.passwordService = $passwordService 
formRealm.credentialsMatcher = $passwordMatcher

<b>jwtService = com.github.panchitoboy.shiro.jwt.JWTGeneratorVerifier
jwtService.expirationSeconds = 10
jwtService.issuer = testApp
jwtService.secretKey = 72AC05536733581EA598CB31BA044D7D03A16B6057093DCF2B780A505607FF7
</b>

tokenMatcher = org.apache.shiro.authc.credential.SimpleCredentialsMatcher
<b>jWTRealm.credentialsMatcher = $tokenMatcher
jWTRealm.jwtService = $jwtService
jWTRealm.authenticationCachingEnabled = true</b>

<b>filterInternal = com.github.panchitoboy.shiro.jwt.filter.JWTOrFormAuthenticationFilter</b>
<b>filterInternal.loginUrl = /resources/test/login</b>

[urls]
/resources/** = filterInternal
</pre>

###1b: Enable Shiro in web.xml

The same file of the tutorial works, but it needs the CdiIniWebEnvironment to enable CDI from pax-shiro-cdi-web
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="3.1" xmlns="http://xmlns.jcp.org/xml/ns/javaee" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd">

    <context-param>
        <param-name>shiroEnvironmentClass</param-name>
        <param-value>org.ops4j.pax.shiro.cdi.web.CdiIniWebEnvironment</param-value>
    </context-param>

    <listener>
        <listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
    </listener>
 
    <filter>
        <filter-name>ShiroFilter</filter-name>
        <filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class>
    </filter>
 
    <filter-mapping>
        <filter-name>ShiroFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

</web-app>
```

##Step 2: Implementing UserDefault and UserRepository

###UserDefault 
UserDefault is the class used to transfer information between your User Store and shiro-jwt. There are only two methods to be implemented: 

    public Object getPrincipal(); // This is the username
    public Object getCredentials(); // This is the password.

### UserRepository
UserRepository is the class that allows the realms to search into your User Store and get the UserDefault object. 

There is one method to be implemented:

    public UserDefault findByUserId(Object userId);

findByUserId is used by FormRealm to get the user's information using the field userId in the json request in the login endpoint


##Step 4: Log in
To log in using the FormRealm you have to make a POST request in the endpoint configured in the shiro.ini file with a JsonObject with these two fields:
- userId
- password

The recommendation is that the response from this endpoint is the JWT to be used in the others endpoints of the application.

To log in using the JwtRealm, you have to add the JWT in the Authorization's header of your request.
