/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

import java.io.Serial;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
public class ModificationException extends Exception {
    @Serial
    private static final long serialVersionUID = 7539434717428521934L;

    public ModificationException(String message) {
        super(message);
    }
}
