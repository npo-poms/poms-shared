/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.List;
import java.util.concurrent.Callable;

import nl.vpro.domain.Roles;

public interface UserService<T extends User> {

    <S> S doAs(String principalId, Callable<S> handler) throws Exception;

    T get(String id);

    List<? extends T> findUsers(String name, int limit);

    T update(T user);

    void delete(T object);

    T currentUser();

    default String currentPrincipalId() {
        T currentUser = currentUser();
        return currentUser == null ? null : currentUser.getPrincipalId();
    }

    void authenticate(String principalId, String password);

    boolean currentUserHasRole(String... roles);

    boolean currentUserHasRole(List<String> roles);

    void authenticate(String principalId);

    default AutoCloseable systemAuthenticate(String principalId, String... roles) {
        authenticate(principalId);
        return this::dropAuthentication;
    }



    void dropAuthentication();

    default boolean isPrivilegedUser() {
        return currentUserHasRole(
            Roles.SUPERADMIN_ROLE,
            Roles.SUPERPROCESS_ROLE,
            Roles.PUBLISHER_ROLE,
            Roles.SUPPORT_ROLE,
            Roles.SYSTEM_ROLE
        );
    }

    default boolean isProcessUser() {
        return currentUserHasRole(
            Roles.PROCESS_ROLE,
            Roles.SUPERPROCESS_ROLE
        );
    }

    default boolean isPublisher() {
        return currentUserHasRole(Roles.PUBLISHER_ROLE);
    }


}
