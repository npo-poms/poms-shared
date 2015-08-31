package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Please note that transactions are only rolled back in case of a runtime exception
 * User: Danny
 * Date: 9-7-12
 * Time: 16:24
 */
public interface TransactionService {
    /**
     * Run callable in new transaction. Transaction is rolled back ONLY in case of a runtime exception
     * @param callable The jobs to be processed in the new transaction
     * @return The result of the callable
     * @throws Exception The exception thrown by the callable
     */
    <T> T executeInNewTransaction(Callable<T> callable) throws Exception;

    <T> T getInNewTransaction(Supplier<T> supplier);

    void  executeInNewTransaction(Runnable runnable);


    <T> T executeInReadonlyTransaction(Callable<T> callable) throws Exception;

    <T> T getInReadonlyTransaction(Supplier<T> supplier);

    void executeInReadonlyTransaction(Runnable runnable);


}
