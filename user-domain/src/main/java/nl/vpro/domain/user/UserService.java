/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;

import java.security.Principal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import javax.transaction.Transactional;

import org.checkerframework.checker.nullness.qual.*;
import org.slf4j.*;

import nl.vpro.domain.Roles;

import static nl.vpro.mdc.MDCConstants.ONBEHALFOF;


public interface UserService<T extends User> {

    /**
     * Given an existing user, and a user obtained from sso or so, determins whether callign {@link #update(User)} is important now.
     */
    boolean needsUpdate(T oldUser, T newUser);


    <S> S doAs(String principalId, Callable<S> handler) throws Exception;

    /**
     *  Do as a certain user, without the need to be logged in already.
     */
    <S> S systemDoAs(String principalId, Callable<S> handler) throws Exception;

    /**
     * From a principal object creates the user if not exists and returns it.
     * @since 5.12
     */
    T get(java.security.Principal authentication);

    Optional<T> get(@NonNull String id);

    default Optional<T> getOnly(@NonNull String id) {
        throw new UnsupportedOperationException();
    }

    /**
     * returns an attached user.
     */
    @Transactional
    default T login(java.security.Principal authentication, Instant timestamp) {
        synchronized (this) {
            T editor = get(authentication);
            editor.setLastLogin(timestamp);
            return update(editor);
        }
    }


    List<? extends T> findUsers(String name, int limit);

    T update(T user);

    void delete(T object);

    Optional<T> currentUser();

    default Optional<String> currentPrincipalId() {
        return Optional.ofNullable(getAuthentication()).map(Principal::getName);
    }

    T authenticate(String principalId, String password);

    Optional<T> authenticate(String principalId);

    default boolean currentUserHasRole(String... roles) {
        return currentUserHasRole(Arrays.asList(roles));
    }

    boolean currentUserHasRole(Collection<String> roles);

    Principal getAuthentication();

    void restoreAuthentication(Principal authentication);

    default boolean isAuthenticated() {
        return getAuthentication() != null;
    }

    /**
     * Default implemention without consideration of the roles. This can be overridden.
     */
    default Logout<T> systemAuthenticate(Trusted trustedSourceToken) {
        authenticate(trustedSourceToken.getPrincipal());
        Logout<T> logout = new Logout<T>() {
            @Override
            public void close() {
                dropAuthentication();
            }
        };
        logout.setUser(currentUser().orElseThrow(IllegalStateException::new));
        return logout;
    }

    void dropAuthentication();

    default boolean isPrivilegedUser() {
        return currentUserHasRole(Roles.PRIVILEGED);
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

    /**
     * Submits callable in the given {@link ExecutorService}, but makes sure that it is executed as the current user
     */
    default <R> Future<R> submit(ExecutorService executorService, Callable<R> callable) {
        return submit(executorService, callable, null);
    }

    /**
     * Submits callable (wrapped by {@link #wrap(Callable, Logger, Boolean)}) in CompletableFuture#supplyAsync
     *
     * @param logger If not <code>null</code> catch exceptions and log as error.
     * @since 5.6
     */

    default <R> CompletableFuture<R> async(Callable<R> callable, Logger logger) {
        Callable<R> wrapped =  wrap(callable, logger, true);
        Supplier<R> supplier  = () -> {
            try {
                return wrapped.call();
            } catch (RuntimeException rte) {
                throw  rte;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return CompletableFuture.supplyAsync(supplier);
    }

    /**
     * Submits callable in the given {@link ExecutorService}, but makes sure that it is executed as the current user and current {@link MDC}
     * @param logger If not <code>null</code> catch exceptions and log as error.
     * @since 5.6
     */
    default <R> Future<R> submit(ExecutorService executorService, Callable<R> callable, Logger logger) {
        return executorService.submit(wrap(callable, logger, null));
    }

    /**
     * Wraps a callable for use by e.g. {@link #submit(ExecutorService, Callable, Logger)} and {@link #async(Callable, Logger)}. This means that current user and {@link MDC} will be restored
     * before {@link Callable#call()}
     * @since 5.6
     */
    default <R> Callable<R> wrap(
        @NonNull  Callable<R> callable,
        @Nullable Logger logger,
        @Nullable Boolean throwExceptions) {

        final boolean throwExceptionsBoolean = throwExceptions == null ? logger == null : throwExceptions;
        Principal authentication;
        try {
            authentication = getAuthentication();
        } catch(Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            authentication = null;
        }
        final Principal onBehalfOf = authentication;
        Map<String, String> copy =  MDC.getCopyOfContextMap();
        if (logger != null) {
            logger.info("Executing on behalf of {}", onBehalfOf);
        }

        return () -> {
            try {
                if (onBehalfOf != null) {
                    try {
                        restoreAuthentication(onBehalfOf);
                    } catch (Exception e) {
                        if (logger != null) {
                            logger.error(e.getMessage());
                        }
                        LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                    }
                }
                if (copy != null) {
                    copy.forEach(MDC::put);
                }
                return callable.call();
            } catch (Exception e) {
                if (logger != null) {
                    logger.error(e.getMessage(), e);
                }
                if (throwExceptionsBoolean) {
                    throw e;
                } else {
                    return null;
                }
            } finally {
                dropAuthentication();
            }
        };
    }


    default Logout<T> restoringAutoClosable() {
        Principal onBehalfOf = getAuthentication();
        if (onBehalfOf != null) {
            try {
                Object principal = onBehalfOf.getClass().getMethod("getPrincipal").invoke(onBehalfOf);
                try {
                    principal = principal.getClass().getMethod("getUsername").invoke(principal);
                } catch(Exception ignored) {

                }
                MDC.put(ONBEHALFOF, ":" + principal);
            } catch (Exception e) {
                MDC.put(ONBEHALFOF, ":" + onBehalfOf.toString());
            }
        }
        return new Logout<T>() {
            @Override
            public void close() {
                try {
                    restoreAuthentication(onBehalfOf);
                } finally {
                    MDC.remove(ONBEHALFOF);
                }
            }
        };
    }

    @Getter
    abstract class  Logout<S extends User> implements AutoCloseable {
        /**
         * The user currently logged in and that will be logout by this.
         */
        @MonotonicNonNull
        private S user;

        public Logout() {
        }

        @Override
        public abstract void close();


        public void setUser(S user) {
            if (this.user != null) {
                throw new IllegalArgumentException();
            }
            this.user = user;
        }

    }

}
