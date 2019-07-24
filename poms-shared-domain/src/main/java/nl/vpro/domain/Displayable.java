/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain;

import java.util.Locale;
import java.util.Optional;

import nl.vpro.i18n.LocalizedString;

/**
 * A displayable has a {@link #getDisplayName()} method, to display the object as a single string (in dutch) to the user.
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
public interface Displayable {

    Locale DEFAULT = new Locale("nl");

    default String getDisplayName() {
        return getDisplayName(DEFAULT).getValue();
    }


    /**
     * Returns a displayable name for this item in the given Locale, or the default locale if not available or not implemented
     * @since 5.11
     */
    default LocalizedString getDisplayName(Locale locale) {
        return LocalizedString.of(getDisplayName(), DEFAULT);
    }


    /**
     * Returns the plural of the display name, if implemented. Otherwise {@link Optional#empty()}
     * @since 5.11
     */
    default Optional<LocalizedString> getPluralDisplayName(Locale locale) {
        return Optional.empty();
    }


     /**
     * @since 5.11
     */
    default Optional<LocalizedString> getPluralDisplayName() {
        return getPluralDisplayName(DEFAULT);
    }


}
