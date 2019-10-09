/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain;

import java.util.Locale;
import java.util.Optional;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.vpro.i18n.Locales;
import nl.vpro.i18n.LocalizedString;

/**
 * A displayable has a {@link #getDisplayName()} method, to display the object as a single string (in dutch) to the user.
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
public interface Displayable {

    @XmlTransient
    @JsonIgnore
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
    @XmlTransient
    @JsonIgnore
    default Optional<LocalizedString> getPluralDisplayName() {
        return getPluralDisplayName(Locales.getDefault());
    }


}
