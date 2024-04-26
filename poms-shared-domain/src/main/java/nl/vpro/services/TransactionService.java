package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.functional.ThrowAnyRunnable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Please note that transactions are only rolled back in case of a runtime exception
 * <p>
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
    <T> T executeInNewTransaction(@NonNull Callable<T> callable) throws Exception;

    <T> T getInNewTransaction(@NonNull Supplier<T> supplier);

    <T> T getInTransaction(@NonNull Supplier<T> supplier);

    void executeInNewTransaction(@NonNull Runnable runnable);

    <T, S> T executeInNewTransaction(S argument, @NonNull Function<S, T> function);

    <T> void executeInNewTransaction(T argument,@NonNull  Consumer<T> consumer);


    <T> T executeInReadonlyTransaction(@NonNull Callable<T> callable) throws Exception;

    <T> T getInReadonlyTransaction(@NonNull Supplier<T> supplier);

    void executeInReadonlyTransaction(@NonNull ThrowAnyRunnable runnable);

    void executeInReadonlyTransaction(@NonNull Runnable runnable);

    <T, S> T executeInReadonlyTransaction(S argument, @NonNull Function<S, T> function);

    <T> void executeInReadonlyTransaction(T argument, @NonNull Consumer<T> consumer);



    @Transactional(propagation = Propagation.NEVER)
    default void ensureNoTransaction() {

    }
}
