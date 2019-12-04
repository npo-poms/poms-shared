/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain;

import java.util.Locale;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.vpro.i18n.Locales;
import nl.vpro.i18n.LocalizedString;

/**
 * A displayable has a {@link #getDisplayName()} method, to display the object as a single string (in dutch) to the user.
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
public interface Displayable {

    default String getDisplayName() {
        return getDisplayName(Locales.getDefault()).getValue();
    }

    /**
     * Returns a displayable name for this item in the given Locale, or the default locale ({@link Locales#getDefault()}) if not available or not implemented
     * @since 5.11
     */
    default LocalizedString getDisplayName(Locale locale) {
        return LocalizedString.of(getDisplayName(), Locales.getDefault());
    }


    /**
     * Returns the plural of the display name, if implemented. Otherwise {@link Optional#empty()}
     * @since 5.11
     */

    default Optional<LocalizedString> getPluralDisplayName(Locale locale) {
        return Optional.empty();
    }


    /**
     * Returns {@link #getDisplayName(Locale)} for the default locale {@link Locales#getDefault()}
     * @since 5.11
     */
    default Optional<LocalizedString> getPluralDisplayName() {
        return getPluralDisplayName(Locales.getDefault());
    }

    /**
     * An url for an icon associated with this displayable object.
     *
     * It may be that this to be interpreted relative to the current 'context path'.
     */
    default Optional<String> getIcon() {
        return Optional.empty();
    }
    /**
     * An url for an icon associated with this displayable object.
     *
     * It may be that this to be interpreted relative to the current 'context path'.
     */
    default Optional<String> getIconClass() {
        return Optional.empty();
    }

    /**
     * Sometimes displayable values are deprecated or experimental. Their value is possible but they should not be
     * displayed in situations where the value is not yet present. Then this can return false.
     */
    @JsonIgnore
    default boolean display() {
        return true;
    }


}
