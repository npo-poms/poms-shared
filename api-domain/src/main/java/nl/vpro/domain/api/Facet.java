/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * A facet is a kind of aggregation. Counts per bucket.
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface Facet<F extends AbstractSearch> {

    /**
     * The filter of facet defines on which parts of the complete index the facet must be applied.
     * <p>
     * This may be implicit ('profiles'), but it can also be done explicitely
     * <p>
     * E.g. apply facets only on all object of type 'ALBUM'.
     * See also {@link SearchableFacet} for limiting the number of facet values you're interested in.
     */
    F getFilter();

    void setFilter(F search);


    default boolean hasFilter() {
        return this.getFilter() != null && this.getFilter().hasSearches();
    }
}
