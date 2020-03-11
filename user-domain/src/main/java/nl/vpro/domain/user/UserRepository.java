/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository<T extends User> {

    T attach(T user);

    Long count();

    Optional<T> get(String id);

    T save(T user);

    T merge(T user);

    void delete(T user);

    void delete(OrganizationEditor<? extends Organization> join);

    List<? extends T> findUsers(String name, int max);

    List<? extends T> list();

    void clear();
}
