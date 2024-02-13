/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.security;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
@Aspect
public class OdiOriginCheck {
    private static final Logger log = LoggerFactory.getLogger(OdiOriginCheck.class);

    private List<String> origins;

    @Before("target(nl.vpro.media.odi.OdiService) && execution(* *(..)) && args(*, *, request, response, ..)")
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        String referrer = request.getHeader("origin");
        if(origins != null && !origins.contains(referrer)) {
            log.warn("Location access forbidden for referrer {}", referrer);
            throw new UnknownOriginException(referrer);
        }
    }

    public void setOrigins(String origins) {
        this.origins = Arrays.asList(origins.split(","));
    }

    public static class UnknownOriginException extends RuntimeException {
        private UnknownOriginException(String message) {
            super("No access for: " + message);
        }
    }
}
