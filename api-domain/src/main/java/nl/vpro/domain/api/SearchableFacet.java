/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
public interface SearchableFacet<T extends AbstractSearch>  {
    boolean hasSubSearch();

    T getSubSearch();

    void setSubSearch(T filter);
}
