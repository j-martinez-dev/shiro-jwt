package com.github.panchitoboy.shiro.jwt.example.jackson;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ObjectMapperProviderExample implements ContextResolver<ObjectMapper> {

    private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private final ObjectMapper objectMapper;

    public ObjectMapperProviderExample() {
        this.objectMapper = new ObjectMapper();
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.objectMapper.setDateFormat(df);
        this.objectMapper.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}
