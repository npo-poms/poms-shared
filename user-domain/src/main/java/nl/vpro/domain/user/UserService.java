/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import nl.vpro.domain.Roles;

import static nl.vpro.mdc.MDCConstants.ONBEHALFOF;

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

    default boolean currentUserHasRole(String... roles) {
        return currentUserHasRole(Arrays.asList(roles));
    }

    boolean currentUserHasRole(List<String> roles);

    void authenticate(String principalId);

    Object getAuthentication();

    void restoreAuthentication(Object authentication);


    /**
     * Default implemention without consideration of the roles. This can be overridden.
     */
    default Logout systemAuthenticate(Trusted trustedSourceToken) {
        authenticate(trustedSourceToken.getPrincipal());
        return this::dropAuthentication;
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
        Object authentication;
        try {
            authentication = getAuthentication();
        } catch(Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            authentication = null;
        }
        final Object onBehalfOf = authentication;
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


    default Logout restoringAutoClosable() {
        Object onBehalfOf = getAuthentication();
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
        return () -> {
            try {
                restoreAuthentication(onBehalfOf);
            } finally {
                MDC.remove(ONBEHALFOF);
            }
        };
    }

    interface  Logout extends AutoCloseable {
        @Override
        void close();
    }

}
