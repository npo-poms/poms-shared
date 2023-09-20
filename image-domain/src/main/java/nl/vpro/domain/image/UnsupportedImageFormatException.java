/*
 * Copyright (C) 2005/2006/2007 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 * Creation date 15-mrt-2007.
 */

package nl.vpro.domain.image;

import java.io.Serial;

/**
 * @author arne
 */
public class UnsupportedImageFormatException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7697045387370472521L;

    public UnsupportedImageFormatException() {
    }

    public UnsupportedImageFormatException(String s) {
        super(s);
    }

    public UnsupportedImageFormatException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UnsupportedImageFormatException(Throwable throwable) {
        super(throwable);
    }
}
