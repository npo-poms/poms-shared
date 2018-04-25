package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a  {@link TransactionService} that will do everything on behalf of one specified user, using
 * a {@link DoAsTransactionService}.
 *
 * @author Michiel Meeuwissen
 * @since 5.7
 */
public class WithUserTransactionServiceImpl implements TransactionService {

    private final DoAsTransactionService doAsTransactionService;
    private final String user;

    public WithUserTransactionServiceImpl(String user, DoAsTransactionService doAsTransactionService) {
        this.doAsTransactionService = doAsTransactionService;
        this.user = user;
    }

    @Override
    public <T> T executeInNewTransaction(Callable<T> callable) throws Exception {
        return doAsTransactionService.executeInNewTransaction(user, callable);
    }

    @Override
    public <T> T getInNewTransaction(Supplier<T> supplier) {
        return doAsTransactionService.getInNewTransaction(user, supplier);

    }

    @Override
    public void executeInNewTransaction(Runnable runnable) {
        doAsTransactionService.executeInNewTransaction(user, runnable);
    }

    @Override
    public <T, S> T executeInNewTransaction(S argument, Function<S, T> function) {
        return doAsTransactionService.executeInNewTransaction(user, argument, function);
    }

    @Override
    public <T> void executeInNewTransaction(T argument, Consumer<T> consumer) {
        doAsTransactionService.executeInNewTransaction(user, argument, consumer);

    }

    @Override
    public <T> T executeInReadonlyTransaction(Callable<T> callable) throws Exception {
        return doAsTransactionService.executeInReadonlyTransaction(user, callable);
    }

    @Override
    public <T> T getInReadonlyTransaction(Supplier<T> supplier) {
        return doAsTransactionService.getInReadonlyTransaction(user, supplier);

    }

    @Override
    public void executeInReadonlyTransaction(Runnable runnable) {
        doAsTransactionService.executeInReadonlyTransaction(user, runnable);
    }

    @Override
    public <T, S> T executeInReadonlyTransaction(S argument, Function<S, T> function) {
        return doAsTransactionService.executeInReadonlyTransaction(user, argument, function);
    }

    @Override
    public <T> void executeInReadonlyTransaction(T argument, Consumer<T> consumer) {
        doAsTransactionService.executeInReadonlyTransaction(user, argument, consumer);

    }

    @Override
    public String toString() {
        return user + "->" + doAsTransactionService;
    }
}
