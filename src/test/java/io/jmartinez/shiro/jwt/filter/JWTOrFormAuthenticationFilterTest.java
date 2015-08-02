package io.jmartinez.shiro.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jmartinez.shiro.jwt.example.jackson.MixInExample;
import io.jmartinez.shiro.jwt.example.rest.ResourceExample;
import io.jmartinez.shiro.jwt.repository.TokenResponse;
import io.jmartinez.shiro.jwt.example.entity.UserDefaultExample;
import io.jmartinez.shiro.jwt.test.Deployments;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.json.JsonObject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class JWTOrFormAuthenticationFilterTest extends Deployments {

    private static String token;
    private ObjectMapper objectMapper;

    @ArquillianResource
    private URL base;

    private WebTarget target;

    @Before
    public void setUp() throws MalformedURLException {
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        target = client.target(URI.create(new URL(base, "resources/test").toExternalForm()));

        objectMapper = new ObjectMapper();
        objectMapper.addMixIn(TokenResponse.class, MixInExample.class);
    }

    @Test(expected = NotAuthorizedException.class)
    @InSequence(1)
    public void securedWithoutToken() throws IOException {
        target.path("/secured").request().accept(MediaType.APPLICATION_JSON).get(TokenResponse.class);
    }

    @Test(expected = NotAuthorizedException.class)
    @InSequence(2)
    public void badLogin() throws IOException {
        UserDefaultExample user = new UserDefaultExample();
        user.setUserId("userId1");
        user.setPassword("password11");
        target.path("/login").request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON), TokenResponse.class);
    }

    @Test
    @InSequence(2)
    public void login() throws IOException {
        UserDefaultExample user = new UserDefaultExample();
        user.setUserId("userId1");
        user.setPassword("password1");
        Response r1 = target.path("/login").request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
        TokenResponse tokenResponse = objectMapper.readValue(r1.readEntity(String.class), TokenResponse.class);
        token = tokenResponse.getToken();
    }

    @Test
    @InSequence(3)
    public void secured() throws IOException {
        Invocation.Builder invocationBuilder = target.path("/secured").request().accept(MediaType.APPLICATION_JSON);
        invocationBuilder.header("Authorization", token);
        String r1 = invocationBuilder.get(String.class);
        Assert.assertEquals("Must have a message", "{\"message\":\"" + ResourceExample.MESSAGE + "\"}", r1);
    }

    @Test(expected = NotAuthorizedException.class)
    @InSequence(4)
    public void securedTokenExpired() throws IOException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        Invocation.Builder invocationBuilder = target.path("/secured").request().accept(MediaType.APPLICATION_JSON);
        invocationBuilder.header("Authorization", token);
        invocationBuilder.get(JsonObject.class);
    }

}
