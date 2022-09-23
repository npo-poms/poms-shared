/*
 * Copyright (C) 2012 All rights reserved
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

    T update(T organization);

    void delete(T organization);

}
