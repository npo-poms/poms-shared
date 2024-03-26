/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository<T extends User> {

    T attach(T user);

    Long count();

    /**
     * Gets user by id (so not the email)
     */
    Optional<T> get(String id);

    T save(T user);

    T merge(T user);

    void delete(T user);

    void delete(OrganizationEditor<? extends Organization> join);

    default List<? extends T> findUsers(String name, int max) {
        return list().stream()
            .filter(u -> u.getDisplayName().toLowerCase().contains(name))
            .limit(max)
            .toList();
    }

    List<? extends T> list();

    void clear();
}
