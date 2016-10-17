/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

public class BroadcasterException extends RuntimeException {
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
