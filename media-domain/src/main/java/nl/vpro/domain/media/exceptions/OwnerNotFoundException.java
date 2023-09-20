/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import java.io.Serial;

import nl.vpro.domain.media.MediaObject;

public class OwnerNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2756165284080124044L;

    public OwnerNotFoundException() {
        super("Owner not found");
    }

    public OwnerNotFoundException(MediaObject owner) {
        super("Owner not found: " + owner);
    }

    public OwnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OwnerNotFoundException(Throwable cause) {
        super(cause);
    }

    public OwnerNotFoundException(String message) {
        super(message);
    }

}
