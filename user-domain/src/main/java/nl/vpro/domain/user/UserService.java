/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;

import java.security.Principal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.*;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import nl.vpro.domain.Roles;
import nl.vpro.i18n.Locales;
import nl.vpro.logging.mdc.MDCConstants;
import nl.vpro.logging.simple.SimpleLogger;

import static nl.vpro.logging.mdc.MDCConstants.ON_BEHALF_OF;


/**
 * The user services provides service related to users. This integrates with spring security, and e.g. with keycloak. It may also support saving the users to a local database. If this in unneeded, because the current system does not have a database backing these methods may be left unimplemented
 */
public interface UserService<T extends User> {

    ThreadPoolExecutor ASYNC_EXECUTOR = new ThreadPoolExecutor(1,
        10000,
        600L, TimeUnit.SECONDS,
        new SynchronousQueue<>());


    /**
     * Given an existing user, and a user obtained from sso or so, determins whether calling {@link #update(User)} is important now.
     */
    boolean needsUpdate(T oldUser, T newUser);


    <S> S doAs(String principalId, Callable<S> handler) throws Exception;

    /**
     *  Do as a certain user, without the need to be logged in already.
     */
    <S> S systemDoAs(String principalId, Callable<S> handler) throws Exception;

     /**
      * Default implementation without consideration of the roles. This can be overridden.
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

    /**
     * From a principal object creates the user if not exists and returns it.
     * @since 5.12
     */
    T get(java.security.Principal authentication);

    Optional<T> get(@NonNull String id);

    /**
     * Just gets a user from local persistence. No implicit creating. This may also give an object that is a bit incomplete, e.g. we don't store roles in the database
     */
    default Optional<T> getOnly(@NonNull String id) {
        throw new UnsupportedOperationException();
    }

    /**
     * Logins in the given principal
     */
    //@Transactional(Transactional.TxType.NEVER) // This make (in mockito 5) this stuff kind of unmockable.
    default T login(java.security.Principal authentication, Instant timestamp) {
        T editor = get(authentication);
        if (timestamp != null) {
            editor.setLastLogin(timestamp);
        }
        return editor;
    }

    /**
     * Searches users in the local database
     */
    List<? extends T> findUsers(String name, int limit);

    /**
     * Updates a user in the local database
     */
    T update(T user);

    /**
     * Deletes user from the local database
     */
    void delete(T object);

    Optional<T> currentUser();

    Optional<T> authenticate(String principalId);

    /**
     * Checks whether current user has at least one of the given roles
     */
    default boolean currentUserHasRole(String... roles) {
        return currentUserHasRole(Arrays.asList(roles));
    }

    default Optional<String> currentPrincipalId() {
        return currentUser().map(User::getPrincipalId);
    }

    boolean currentUserHasRole(Collection<String> roles);

    Principal getAuthentication();

    void restoreAuthentication(Principal authentication);

    default boolean isAuthenticated() {
        return getAuthentication() != null;
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

    /**
     * See {@link Roles#PUBLISHER_ROLE}
     */
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
     * Defaulting version of {@link #async(Callable, SimpleLogger, ExecutorService)}, where the executor service is {@link #ASYNC_EXECUTOR}
     *
     * @param logger If not <code>null</code> catch exceptions and log as error.
     * @since 5.6
     */
    default <R> CompletableFuture<R> async(Callable<R> callable, SimpleLogger logger) {
        return async(callable, logger, ASYNC_EXECUTOR);
    }

    /**
     * Submits callable (wrapped by {@link #wrap(Callable, SimpleLogger, Boolean, boolean)} )}) in CompletableFuture#supplyAsync.
     * <p>
     * This makes sure that the job is running as the current user, and for example also that the current MDC is copied to the other thread.
     * <p>
     * Note that if you use {@link CompletableFuture#thenAccept(Consumer)} or something similar that these will not be run in the same context. You can wrapp those with {@link #wrap(Callable, SimpleLogger, Boolean, boolean)} yourself.
     *
     * @param callable The job to run asynchronously
     * @param logger If not <code>null</code> catch exceptions and log as error.
     *
     * @since 5.16
     */
    default <R> CompletableFuture<R> async(Callable<R> callable, SimpleLogger logger, ExecutorService executor) {
        Callable<R> wrapped =  wrap(callable, logger, true, true); // Current MDC will be copied and stored  and restores just before calling the unwrapped callable
        Supplier<R> supplier  = () -> {
            try {
                return wrapped.call();
            } catch (RuntimeException rte) {
                throw  rte;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return CompletableFuture.supplyAsync(supplier, executor);
    }

    /**
     * Submits callable in the given {@link ExecutorService}, but makes sure that it is executed as the current user and current {@link MDC}
     * @param logger If not <code>null</code> catch exceptions and log as error.
     * @since 5.6
     */
    default <R> Future<R> submit(ExecutorService executorService, Callable<R> callable, SimpleLogger logger) {
        return executorService.submit(
            wrap(callable, logger, null, true)
        );
    }

    /**
     * Wraps a callable for use by e.g. {@link #submit(ExecutorService, Callable, SimpleLogger)} and {@link #async(Callable, SimpleLogger)}. This means that current user and {@link MDC} will be restored
     * before {@link Callable#call()}
     * @since 5.6
     */
    default <R> Callable<R> wrap(
        @NonNull  Callable<R> callable,
        @Nullable SimpleLogger logger,
        @Nullable Boolean throwExceptions,
        boolean clearMDC) {

        final boolean throwExceptionsBoolean = throwExceptions == null ? logger == null : throwExceptions;
        Principal authentication;
        try {
            authentication = getAuthentication();
        } catch(Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
            authentication = null;
        }
        final Principal onBehalfOf = authentication;
        final Map<String, String> copy =  MDC.getCopyOfContextMap();
        if (logger != null) {
            logger.info("Executing on behalf of {}", onBehalfOf);
        }
        final Locale currentLocale = Locales.getDefault();

        return () -> {
            MDC.clear(); // Running in an unknown thread, making sure MDC is clean
            try (AutoCloseable restore = Locales.with(currentLocale)){
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
                    // and make sure that
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
                MDCConstants.onBehalfOf(principal.toString());
            } catch (Exception e) {
                MDCConstants.onBehalfOf(onBehalfOf.toString());
            }
        }
        return new Logout<T>() {
            @Override
            public void close() {
                try {
                    restoreAuthentication(onBehalfOf);
                } finally {
                    MDC.remove(ON_BEHALF_OF);
                }
            }
        };
    }

    @Getter
    abstract class  Logout<S extends User> implements AutoCloseable {

        public static <T extends User> Logout<T> nop() {
            return new Logout<T>() {
                @Override
                public void close() {

                }
            };
        }
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
