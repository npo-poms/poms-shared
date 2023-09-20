/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import java.io.Serial;

import nl.vpro.domain.user.Organization;

public class OrganizationNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -923897830621230741L;

    public OrganizationNotFoundException() {
        super("Organization not found");
    }

    public OrganizationNotFoundException(Organization organization) {
        super("Organization not found: " + organization);
    }

    public OrganizationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrganizationNotFoundException(Throwable cause) {
        super(cause);
    }

    public OrganizationNotFoundException(String message) {
        super(message);
    }

}
