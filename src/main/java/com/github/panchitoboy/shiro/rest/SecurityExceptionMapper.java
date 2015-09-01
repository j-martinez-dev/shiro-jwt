package com.github.panchitoboy.shiro.rest;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.shiro.ShiroException;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<ShiroException> {

    @Context
    HttpServletRequest req;

    @Override
    public Response toResponse(ShiroException exception) {
        JsonArrayBuilder array = Json.createArrayBuilder();
        array.add(exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(array.build()).type(MediaType.APPLICATION_JSON).build();
    }

}
