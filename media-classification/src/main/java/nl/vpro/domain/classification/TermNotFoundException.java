/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.classification;

import java.io.Serial;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class TermNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 9148100738513701534L;

    public TermNotFoundException(String code) {
        super("No term for code or reference "  + code);
    }
}
