/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.backend;

import java.io.Serial;

import nl.vpro.domain.NotFoundException;

/**
 *
 */
public class ImageNotFoundException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 1607385729781522225L;

    public ImageNotFoundException(long identifier, String message) {
        this(identifier, message, null);
    }

    public ImageNotFoundException(String identifier, String message) {
        super(identifier, message);
    }

    public ImageNotFoundException(String identifier, String message, Exception cause) {
        super(identifier, message, cause);
    }

    public ImageNotFoundException(long identifier, String message, Exception cause) {
        super(identifier == -1 ? null : String.valueOf(identifier), message, cause);
    }

}
