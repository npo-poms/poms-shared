/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile.exception;

import nl.vpro.domain.NotFoundException;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class ProfileNotFoundException extends NotFoundException {
    public ProfileNotFoundException(String name) {
        super(name, "No profile for " + name);
    }
}
