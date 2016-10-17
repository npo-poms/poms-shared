/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import nl.vpro.domain.media.MediaObject;

public class OwnerNotFoundException extends RuntimeException {

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
