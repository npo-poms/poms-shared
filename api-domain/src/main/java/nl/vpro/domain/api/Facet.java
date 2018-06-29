/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * A facet is a kind of aggregation. Counts per bucket.
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface Facet<T extends AbstractSearch> {

    /**
     * The filter of facet defines on which parts of the complete index the facet must be applied.
     *
     * This may be implicit ('profiles'), but it can also be done explicitely
     *
     * E.g. apply facets only on all object of type 'ALBUM'.
     */
    T getFilter();

    void setFilter(T search);
}
