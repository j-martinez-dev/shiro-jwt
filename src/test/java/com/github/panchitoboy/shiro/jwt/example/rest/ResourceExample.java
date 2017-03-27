package com.github.panchitoboy.shiro.jwt.example.rest;

import com.github.panchitoboy.shiro.interceptor.SecurityChecked;
import com.github.panchitoboy.shiro.jwt.JWTGeneratorVerifier;
import com.github.panchitoboy.shiro.jwt.example.entity.UserDefaultExample;
import com.github.panchitoboy.shiro.jwt.repository.TokenResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;

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

@Path("test")
@Stateless
@SecurityChecked(type = "JAX-RS")
public class ResourceExample {

    public static final String MESSAGE = "It works!!";


    @Inject
    JWTGeneratorVerifier jwtService;

    public void setJwtService(JWTGeneratorVerifier jwtGeneratorVerifier) {
        this.jwtService = jwtGeneratorVerifier;
    }

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login() throws Exception {

        UserDefaultExample user = (UserDefaultExample) SecurityUtils.getSubject().getPrincipal();
        TokenResponse token = jwtService.createToken(user);
        return Response.status(200).entity(token).build();
    }

    @GET
    @Path("secured")
    @Produces(MediaType.APPLICATION_JSON)
    @RequiresRoles("service1")
    public Response secured() throws Exception {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("message", MESSAGE);
        return Response.status(200).entity(json.build()).build();
    }
}
