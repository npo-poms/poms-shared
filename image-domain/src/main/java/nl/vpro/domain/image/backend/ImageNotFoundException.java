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

    public ImageNotFoundException(long s, String message) {
        this(s, message, null);
    }

    public ImageNotFoundException(String s, String message) {
        super(s, message);
    }

    public ImageNotFoundException(String s, String message, Exception cause) {
        super(s, message, cause);
    }

    public ImageNotFoundException(long s, String message, Exception cause) {
        super(s == -1 ? null : String.valueOf(s), message, cause);
    }

}
