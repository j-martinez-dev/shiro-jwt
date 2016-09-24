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

Also, it's important to say that there are two realms defined: 
- $jWTRealm [JWTRealm] (https://github.com/panchitoboy/shiro-jwt/blob/master/src/main/java/com/github/panchitoboy/shiro/jwt/realm/JWTRealm.java)
- $formRealm [FormRealm] (https://github.com/panchitoboy/shiro-jwt/blob/master/src/main/java/com/github/panchitoboy/shiro/jwt/realm/FormRealm.java)
 
<pre>
[main]
builtInCacheManager = org.apache.shiro.cache.MemoryConstrainedCacheManager

<b>securityManager.realms = $jWTRealm, $formRealm</b>
securityManager.subjectDAO.sessionStorageEvaluator.sessionStorageEnabled = false
securityManager.cacheManager = $builtInCacheManager

passwordMatcher = org.apache.shiro.authc.credential.PasswordMatcher
passwordMatcher.passwordService = $passwordService 
formRealm.credentialsMatcher = $passwordMatcher

tokenMatcher = org.apache.shiro.authc.credential.SimpleCredentialsMatcher
<b>jWTRealm.credentialsMatcher = $tokenMatcher</b>

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
        
    <session-config>
        <session-timeout>30</session-timeout>
    </session-config>
</web-app>
```

##Step 2: Implementing UserDefault and UserRepository

###UserDefault 
UserDefault is the class used to transfer information between your User Store and shiro-jwt. There are only two methods to be implemented: 

    public Object getPrincipal();
    public Object getCredentials();

### UserRepository
UserRepository is the class that allows the realms to search into your User Store and get the UserDefault object. 

There are two key methods to be implemented:

    public UserDefault findByUserId(Object userId);
    public UserDefault findById(Object id);

findByUserId is used by FormRealm to get the user's information using the field userId in the json request in the login endpoint

findById is used by JWTRealm to get the user's information from the sub field in the JWT.

### What's the difference between the two methods?

The difference is the information that you stock in the field sub in the JWT. If you chose to use the same id that you received in findByUserId (exammple: email address) both methods must be the same. But, if you decide to use another information (example: numeric primary key of the user in the database) you have to implement a specific way to search the user.

##Step 3: Generate tokens
UserRepository.createToken has a default implementation to generate the tokens. You can adapt it for you convenience. 

##Step 4: Log in
To log in using the FormRealm you have to make a POST request in the endpoint configured in the shiro.ini file with a JsonObject with these two fields:
- userId
- password

The recommendation is that the response from this endpoint is the JWT to be used in the others endpoints of the application.

To log in using the JwtRealm, you have to add the JWT in the Authorization's header of your request.
