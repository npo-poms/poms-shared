/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public interface NameableLimitableFacet<T extends AbstractSearch, S extends AbstractSearch> extends SearchableFacet<T, S>, Nameable, LimitableFacet<T> {

}
