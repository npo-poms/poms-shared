package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

/**
 * Please note that transactions are only rolled back in case of a runtime exception
 *
 * Also note that in new transactions the currently existing hibernate filters will be gone.
 *
 * @author Danny Sedney
 */
public interface TransactionService {
    /**
     * Run callable in new transaction. Transaction is rolled back ONLY in case of a runtime exception
     * @param callable The jobs to be processed in the new transaction
     * @return The result of the callable
     * @throws Exception The exception thrown by the callable
     */
    <T> T executeInNewTransaction(@Nonnull Callable<T> callable) throws Exception;

    <T> T getInNewTransaction(@Nonnull Supplier<T> supplier);

    void executeInNewTransaction(@Nonnull Runnable runnable);

    <T, S> T executeInNewTransaction(S argument, @Nonnull Function<S, T> function);

    <T> void executeInNewTransaction(T argument,@Nonnull  Consumer<T> consumer);


    <T> T executeInReadonlyTransaction(@Nonnull Callable<T> callable) throws Exception;

    <T> T getInReadonlyTransaction(@Nonnull Supplier<T> supplier);

    void executeInReadonlyTransaction(@Nonnull Runnable runnable);

    <T, S> T executeInReadonlyTransaction(S argument, @Nonnull Function<S, T> function);

    <T> void executeInReadonlyTransaction(T argument, @Nonnull Consumer<T> consumer);


}
