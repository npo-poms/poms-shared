package nl.vpro.services;

import java.util.concurrent.Callable;

/**
 * Please note that transactions are only rolled back in case of a runtime exception
 * User: Danny
 * Date: 9-7-12
 * Time: 16:24
 */
public interface TransactionService {
    /**
     * Run callable in new transaction. Transaction is rolled back ONLY in case of a runtime exception
     * @param callable
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T executeInNewTransaction(Callable<T> callable) throws Exception;

    void  executeInNewTransaction(Runnable runnable) throws Exception;

}
