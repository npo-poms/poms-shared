/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile.exception;

import java.io.Serial;

import nl.vpro.domain.NotFoundException;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class ProfileNotFoundException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 2575657362790907766L;

    public ProfileNotFoundException(String name) {
        super(name, "Unknown profile \"" + name + "\"");
    }
}
