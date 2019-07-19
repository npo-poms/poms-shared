/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain;

import java.util.Locale;

/**
 * A displayable has a {@link #getDisplayName()} method, to display the object as a single string (in dutch) to the user.
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
public interface Displayable {

    Locale DEFAULT = new Locale("nl");

    default String getDisplayName() {
        return getDisplayName(DEFAULT);
    }


    /**
     * @since 5.11
     */
    default String getDisplayName(Locale locale) {
        return getDisplayName();
    }


    /**
     * @since 5.11
     */
    default String getPluralDisplayName(Locale locale) {
        throw new UnsupportedOperationException();
    }


     /**
     * @since 5.11
     */
    default String getPluralDisplayName() {
        return getPluralDisplayName(DEFAULT);
    }


}
