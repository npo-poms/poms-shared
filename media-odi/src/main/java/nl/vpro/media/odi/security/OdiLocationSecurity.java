/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.security;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import nl.vpro.domain.media.Location;
import nl.vpro.esper.service.EventServiceProvider;
import nl.vpro.media.odi.util.InetAddressUtil;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@Aspect
public class OdiLocationSecurity {

    @Autowired
    private EventServiceProvider eventService;

    @Before("execution(* nl.vpro.media.odi.LocationProducer.produce(..)) && args(location, request, ..)")
    public void handle(Location location, HttpServletRequest request) {
        eventService.send(new OdiEvent(
            location.getProgramUrl(),
            SecurityContextHolder.getContext().getAuthentication().getName().toString(),
            InetAddressUtil.getClientHost(request)
        ));
    }

    public void setEventService(EventServiceProvider eventService) {
        this.eventService = eventService;
    }
}
