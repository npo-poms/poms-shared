package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.user.Trusted;

/**
 * This is a  {@link TransactionService} that will do everything on behalf of one specified user, using
 * a {@link DoAsTransactionService}.
 *
 * @author Michiel Meeuwissen
 * @since 5.7
 */
public class WithUserTransactionServiceImpl implements TransactionService {

    private final DoAsTransactionService doAsTransactionService;
    private final Trusted user;

    public WithUserTransactionServiceImpl(@NonNull Trusted user, @NonNull DoAsTransactionService doAsTransactionService) {
        this.doAsTransactionService = doAsTransactionService;
        this.user = user;
    }

    @Override
    public <T> T executeInNewTransaction(@NonNull Callable<T> callable) {
        return doAsTransactionService.executeInNewTransaction(user, callable);
    }

    @Override
    public <T> T executeInTransaction(@NonNull Callable<T> callable) {
        return doAsTransactionService.executeInTransaction(user, callable);
    }


    @Override
    public <T> T getInNewTransaction(@NonNull Supplier<T> supplier) {
        return doAsTransactionService.getInNewTransaction(user, supplier);
    }


    @Override
    public <T> T getInTransaction(@NonNull Supplier<T> supplier) {
        return doAsTransactionService.getInTransaction(user, supplier);
    }


    @Override
    public void executeInNewTransaction(@NonNull Runnable runnable) {
        doAsTransactionService.executeInNewTransaction(user, runnable);
    }

    @Override
    public void executeInTransaction(@NonNull Runnable runnable) {
        doAsTransactionService.executeInTransaction(user, runnable);
    }


    @Override
    public <T, S> T executeInNewTransaction(S argument, @NonNull Function<S, T> function) {
        return doAsTransactionService.executeInNewTransaction(user, argument, function);
    }

    @Override
    public <T> void executeInNewTransaction(T argument, @NonNull Consumer<T> consumer) {
        doAsTransactionService.executeInNewTransaction(user, argument, consumer);
    }

    @Override
    public <T> T executeInReadonlyTransaction(@NonNull Callable<T> callable) {
        return doAsTransactionService.executeInReadonlyTransaction(user, callable);
    }

    @Override
    public <T> T getInReadonlyTransaction(@NonNull Supplier<T> supplier) {
        return doAsTransactionService.getInReadonlyTransaction(user, supplier);
    }

    @Override
    public void executeInReadonlyTransaction(@NonNull Runnable runnable) {
        doAsTransactionService.executeInReadonlyTransaction(user, runnable);
    }

    @Override
    public <T, S> T executeInReadonlyTransaction(S argument, @NonNull Function<S, T> function) {
        return doAsTransactionService.executeInReadonlyTransaction(user, argument, function);
    }

    @Override
    public <T> void executeInReadonlyTransaction(T argument, @NonNull Consumer<T> consumer) {
        doAsTransactionService.executeInReadonlyTransaction(user, argument, consumer);
    }

    @Override
    public String toString() {
        return user + "->" + doAsTransactionService;
    }
}
