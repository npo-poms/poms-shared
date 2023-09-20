/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.security;

/**
 * See https://jira.vpro.nl/browse/MSE-
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class OdiEvent {
    private final String programUrl;

    private final String principalId;

    private final String address;

    public OdiEvent(String programUrl, String principalId, String address) {
        this.programUrl = programUrl;
        this.principalId = principalId;
        this.address = address;
    }

    public String getProgramUrl() {
        return programUrl;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public String getAddress() {
        return address;
    }
}
