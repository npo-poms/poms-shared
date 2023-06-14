/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import java.io.Serial;

public class BroadcasterException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1794747558440101218L;

    public BroadcasterException() {
    }

    public BroadcasterException(String s) {
        super(s);
    }

    public BroadcasterException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public BroadcasterException(Throwable throwable) {
        super(throwable);
    }
}
