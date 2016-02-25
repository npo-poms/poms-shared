/*
 * Copyright (C) 2005/2006/2007 All rights reserved
 * VPRO The Netherlands
 * Creation date 15-mrt-2007.
 */

package nl.vpro.domain.image;

/**
 * @author arne
 * @version $Id$
 */
public class UnsupportedImageFormatException extends RuntimeException {

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
