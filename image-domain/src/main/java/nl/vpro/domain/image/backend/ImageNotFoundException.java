/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.backend;

import nl.vpro.domain.NotFoundException;

public class ImageNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1607385729781522225L;

    public ImageNotFoundException(long s, String message) {
        this(String.valueOf(s), message);
    }

    public ImageNotFoundException(String s, String message) {
        super(s, message);
    }

    public ImageNotFoundException(String s, String message, Exception cause) {
        super(s, message, cause);
    }

}
