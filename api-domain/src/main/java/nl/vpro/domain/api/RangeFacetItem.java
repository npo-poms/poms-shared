/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public interface RangeFacetItem<T extends Comparable<T>> extends RangeFacet<T> {

    String getName();

    T getBegin();

    T getEnd();

}
