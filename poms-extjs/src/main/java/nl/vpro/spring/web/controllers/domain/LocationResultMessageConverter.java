/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.spring.web.controllers.domain;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import nl.vpro.media.odi.util.LocationResult;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
public class LocationResultMessageConverter implements HttpMessageConverter<LocationResult> {
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return clazz.isAssignableFrom(LocationResult.class) && MediaType.TEXT_PLAIN.equals(mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(MediaType.TEXT_PLAIN);
    }

    @Override
    public LocationResult read(Class<? extends LocationResult> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException("Can't read LocationResult");
    }

    @Override
    public void write(LocationResult locationResult, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        String output = locationResult == null || locationResult.getProgramUrl() == null ? "" : locationResult.getProgramUrl();
        outputMessage.getBody().write(output.getBytes("UTF-8"));
    }
}
