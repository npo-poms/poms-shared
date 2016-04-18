package nl.vpro.domain.services;

import java.util.concurrent.Callable;

import nl.vpro.services.TransactionService;

/**
 * @author Michiel Meeuwissen
 * @since 3.6
 */
public interface DoAsTransactionService extends TransactionService {


    <T> T executeInNewTransaction(String user, Callable<T> callable) throws Exception;

    void executeInNewTransaction(String user,  Runnable runnable);

    <T> T executeInReadonlyTransaction(String user, Callable<T> callable) throws Exception;

    void executeInReadonlyTransaction(String user, Runnable runnable);
}
