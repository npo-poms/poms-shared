/*
 * Copyright (C) 2011 All rights reserved
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
