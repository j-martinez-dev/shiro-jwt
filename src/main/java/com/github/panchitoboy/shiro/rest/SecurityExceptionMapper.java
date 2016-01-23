package com.github.panchitoboy.shiro.rest;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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

    public static final String RESOURCE_BUNDLE_FILE = "messages";

    @Override
    public Response toResponse(ShiroException exception) {
        JsonArrayBuilder array = Json.createArrayBuilder();
        array.add(getMessage(exception.getMessage(), req));
        return Response.status(Response.Status.BAD_REQUEST).entity(array.build()).type(MediaType.APPLICATION_JSON).build();
    }

    private String getMessage(String key, HttpServletRequest req) {
        Locale currentLocale = req.getLocale().stripExtensions();
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_FILE, currentLocale);
            String message = resourceBundle.getString(key);
            return message;
        } catch (MissingResourceException ex) {
            return key;
        }
    }

}
