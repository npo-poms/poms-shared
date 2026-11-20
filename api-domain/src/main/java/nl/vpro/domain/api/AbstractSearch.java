/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;

import nl.vpro.domain.api.media.MediaSearch;
import nl.vpro.domain.api.media.MemberRefSearch;
import nl.vpro.domain.api.page.AssociationSearch;
import nl.vpro.domain.api.page.PageSearch;
import nl.vpro.util.IterableWrapper;

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


    /**
     * @param collections {@link IterableWrapper}s (like {@link MatcherList}) or {@link Iterable}s (or {@code null}s)
     * @return whether at least one of the arguments is not empty
     */
    protected static boolean atLeastOneHasSearches(Object... collections) {
        for (Object col : collections) {
            boolean notEmpty = switch (col) {
                case null -> false;
                case IterableWrapper<?> wrapper -> !wrapper.isEmpty();
                case Iterable<?> iterable -> iterable.iterator().hasNext();
                default -> throw new IllegalArgumentException("Not a collection " + col);
            };
            if (notEmpty) {
                return true;
            }
        }
        return false;
    }

}
