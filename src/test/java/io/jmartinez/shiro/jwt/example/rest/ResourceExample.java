package io.jmartinez.shiro.jwt.example.rest;

import io.jmartinez.shiro.jwt.example.entity.UserDefaultExample;
import io.jmartinez.shiro.jwt.repository.TokenResponse;
import io.jmartinez.shiro.jwt.repository.UserRepository;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;

@Path("test")
@Stateless
public class ResourceExample {

    public static final String MESSAGE = "It works!!";

    @Inject
    UserRepository shiroBoundary;

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login() throws Exception {

        UserDefaultExample user = (UserDefaultExample) SecurityUtils.getSubject().getPrincipal();
        TokenResponse token = shiroBoundary.createToken(user);
        return Response.status(200).entity(token).build();
    }

    @GET
    @Path("secured")
    @Produces(MediaType.APPLICATION_JSON)
    public Response secured() throws Exception {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("message", MESSAGE);
        return Response.status(200).entity(json.build()).build();
    }
}
