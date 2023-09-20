/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

import java.io.Serial;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
public class GTAAConflict extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 9070035685737918751L;

    public GTAAConflict(String message) {
        super(message);
    }
}
