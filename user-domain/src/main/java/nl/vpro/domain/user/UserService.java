/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.List;
import java.util.concurrent.Callable;

public interface UserService<T extends User> {

    <S> S doAs(String principalId, Callable<S> handler) throws Exception;

    T get(String id);

    List<? extends T> findUsers(String name, int limit);

    T update(T user);

    void delete(T object);

    T currentUser();

    void authenticate(String principalId, String password);

    boolean currentUserHasRole(String... roles);

    boolean currentUserHasRole(List<String> roles);

    void authenticate(String principalId);

    void dropAuthentication();

}
