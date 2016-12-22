/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.classification;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class TermNotFoundException extends RuntimeException {

    public TermNotFoundException(String code) {
        super("No term for code or reference "  + code);
    }
}
