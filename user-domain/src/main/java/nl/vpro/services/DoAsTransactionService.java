package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author Michiel Meeuwissen
 * @since 3.6
 */
public interface DoAsTransactionService extends TransactionService {


    <T> T executeInNewTransaction(String user, Callable<T> callable) throws Exception;

    <T, S> T executeInNewTransaction(String user, S argument, Function<S, T> function);

    void executeInNewTransaction(String user,  Runnable runnable);

    <T> T executeInReadonlyTransaction(String user, Callable<T> callable) throws Exception;

    <T, S> T executeInReadonlyTransaction(String user, S argument, Function<S, T> function);


    void executeInReadonlyTransaction(String user, Runnable runnable);
}
