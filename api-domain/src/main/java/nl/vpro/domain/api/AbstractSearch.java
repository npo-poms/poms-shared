/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import nl.vpro.domain.api.media.MediaSearch;
import nl.vpro.domain.api.media.MemberRefSearch;
import nl.vpro.domain.api.page.AssociationSearch;
import nl.vpro.domain.api.page.PageSearch;

/**
 * A Search interface but JAXB won't handle interfaces
 * <p>
 * This is a {@link Matcher} that also has a method {@link #hasSearches}. So in other words the matching is
 *
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({MediaSearch.class, PageSearch.class, MemberRefSearch.class, AssociationSearch.class})
@XmlTransient
public abstract class AbstractSearch<V> extends AbstractMatcher<V> {

    public abstract boolean hasSearches();

    protected AbstractSearch() {

    }

    protected AbstractSearch(Match match) {
        super(match);
    }


    protected static boolean atLeastOneHasSearches(Iterable<?>... collections) {
        for (Iterable<?> col : collections) {
            if (col != null) {
                if (col instanceof MatcherList<?, ?>) {
                    if (!((MatcherList) col).isEmpty()) {
                        return true;
                    }
                } else {
                    if (col.iterator().hasNext()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
