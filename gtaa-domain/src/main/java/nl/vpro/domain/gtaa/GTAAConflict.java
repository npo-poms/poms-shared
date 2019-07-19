/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
public class GTAAConflict extends RuntimeException {
    public GTAAConflict(String message) {
        super(message);
    }
}
