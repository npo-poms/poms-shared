package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * @author Michiel Meeuwissen
 * @since 3.6
 */
public interface DoAsTransactionService extends TransactionService {


    <T> T executeInNewTransaction(@Nonnull String user, @Nonnull Callable<T> callable) throws Exception;

    <T, S> T executeInNewTransaction(@Nonnull String user, S argument, @Nonnull  Function<S, T> function);

    void executeInNewTransaction(@Nonnull String user,  @Nonnull  Runnable runnable);

    <T> T executeInReadonlyTransaction(@Nonnull String user, @Nonnull  Callable<T> callable) throws Exception;

    <T, S> T executeInReadonlyTransaction(@Nonnull String user, S argument, @Nonnull  Function<S, T> function);


    void executeInReadonlyTransaction(@Nonnull String user, @Nonnull  Runnable runnable);

    <T> void executeInReadonlyTransaction(@Nonnull String user, T argument, @Nonnull  Consumer<T> consumer);


    <T> T getInNewTransaction(@Nonnull String user, @Nonnull Supplier<T> supplier);

    <T> T getInReadonlyTransaction(@Nonnull String user, @Nonnull  Supplier<T> supplier);


    <T> void executeInNewTransaction(@Nonnull String user, T argument, @Nonnull  Consumer<T> consumer);

    /**
     * Makes a {@link TransactionService} which does everything implicit as a certain given user.
     * @see WithUserTransactionServiceImpl
     * @param user principalid
     * @param doAsTransactionService The DoAsTransactionService doing the 'do as' functionality
     * @since 5.7
     */
    @Nonnull
    static TransactionService as(
        @Nonnull String user,
        @Nonnull DoAsTransactionService doAsTransactionService) {
        return new WithUserTransactionServiceImpl(user, doAsTransactionService);
    }


}
