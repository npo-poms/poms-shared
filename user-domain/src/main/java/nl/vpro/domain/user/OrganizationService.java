/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.List;

public interface OrganizationService<T extends Organization> {

    /**
     * Finds the organization by its main id
     *
     * @return <code>null</code> if not found
     */
    T find(String id);

    List<T> findAll();

    default T update(T organization) {
        throw new UnsupportedOperationException();
    }

    default void delete(T organization) {
        throw new UnsupportedOperationException();
    }

}
