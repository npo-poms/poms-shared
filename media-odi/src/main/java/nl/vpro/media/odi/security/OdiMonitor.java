/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.security;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.espertech.esper.client.UpdateListener;

import nl.vpro.esper.listener.EventLogger;
import nl.vpro.esper.service.EventServiceProvider;
import nl.vpro.esper.service.Statement;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class OdiMonitor {

    @Value("${odi.alert}")
    private int odiAlert = 20;

    @Autowired
    private EventServiceProvider eventService;

    private final EventLogger securityBreachLogger = new EventLogger("nl.vpro.log.odi", "User {} requested {} ODI sources during the last 5 minutes", "principalId", "count(*)");

    @PostConstruct
    private void init() {
        addStatement("select principalId, address, programUrl, count(*) from nl.vpro.media.odi.security.OdiEvent.win:time(1 min) group by principalId having count(*) > " + odiAlert, securityBreachLogger) ;
    }

    private void addStatement(String statement, UpdateListener... listeners) {
        Statement esperStatement = new Statement(statement);
        esperStatement.setListeners(listeners);
        eventService.addStatement(esperStatement);
    }
}
