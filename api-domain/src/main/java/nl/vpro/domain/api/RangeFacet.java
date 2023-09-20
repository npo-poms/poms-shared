/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * @author Roelof Jan Koekoek
 * @since 3.1
 */
public interface RangeFacet<T extends Comparable<T>> {

    boolean matches(T begin, T end);

}
