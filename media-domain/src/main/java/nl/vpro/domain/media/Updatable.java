/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

public interface Updatable<T> {

    /**
     * Copies all fields from argument
     *
     * @param from
     */
    void update(T from);

}
