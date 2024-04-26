package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.functional.ThrowAnyRunnable;

import nl.vpro.domain.user.Trusted;

/**
 * @author Michiel Meeuwissen
 * @since 3.6
 */
public interface DoAsTransactionService extends TransactionService {


    <T> T executeInNewTransaction(@NonNull Trusted user, @NonNull Callable<T> callable);

    <T, S> T executeInNewTransaction(@NonNull Trusted user, S argument, @NonNull  Function<S, T> function);

    void executeInNewTransaction(@NonNull Trusted user, @NonNull  Runnable runnable);


    <T> T executeInReadonlyTransaction(@NonNull Trusted user, @NonNull  Callable<T> callable);

    <T, S> T executeInReadonlyTransaction(@NonNull Trusted user, S argument, @NonNull  Function<S, T> function);


    void executeInReadonlyTransaction(@NonNull Trusted user, @NonNull  Runnable runnable);

    void executeInReadonlyTransaction(@NonNull Trusted user, @NonNull ThrowAnyRunnable runnable);


    <T> void executeInReadonlyTransaction(@NonNull Trusted user, T argument, @NonNull  Consumer<T> consumer);

    <T> T getInNewTransaction(@NonNull Trusted user, @NonNull Supplier<T> supplier);

    <T> T getInTransaction(@NonNull Trusted user, @NonNull Supplier<T> supplier);

    <T> T getInReadonlyTransaction(@NonNull Trusted user, @NonNull  Supplier<T> supplier);

    <T> void executeInNewTransaction(@NonNull Trusted user, T argument, @NonNull  Consumer<T> consumer);

    /**
     * Makes a {@link TransactionService} which does everything implicit as a certain given user.
     * @see WithUserTransactionServiceImpl
     * @param user principalid
     * @param doAsTransactionService The DoAsTransactionService doing the 'do as' functionality
     * @since 5.7
     */
    @NonNull
    static TransactionService as(
        @NonNull Trusted user,
        @NonNull DoAsTransactionService doAsTransactionService) {
        return new WithUserTransactionServiceImpl(user, doAsTransactionService);
    }


}
