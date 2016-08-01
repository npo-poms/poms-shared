/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import nl.vpro.domain.user.Organization;

public class OrganizationNotFoundException extends RuntimeException {

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
