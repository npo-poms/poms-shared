package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

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

    public WithUserTransactionServiceImpl(@Nonnull Trusted user, @Nonnull DoAsTransactionService doAsTransactionService) {
        this.doAsTransactionService = doAsTransactionService;
        this.user = user;
    }

    @Override
    public <T> T executeInNewTransaction(@Nonnull Callable<T> callable) throws Exception {
        return doAsTransactionService.executeInNewTransaction(user, callable);
    }

    @Override
    public <T> T getInNewTransaction(@Nonnull  Supplier<T> supplier) {
        return doAsTransactionService.getInNewTransaction(user, supplier);

    }

    @Override
    public void executeInNewTransaction(@Nonnull  Runnable runnable) {
        doAsTransactionService.executeInNewTransaction(user, runnable);
    }

    @Override
    public <T, S> T executeInNewTransaction(S argument, @Nonnull Function<S, T> function) {
        return doAsTransactionService.executeInNewTransaction(user, argument, function);
    }

    @Override
    public <T> void executeInNewTransaction(T argument, @Nonnull Consumer<T> consumer) {
        doAsTransactionService.executeInNewTransaction(user, argument, consumer);

    }

    @Override
    public <T> T executeInReadonlyTransaction(@Nonnull Callable<T> callable) throws Exception {
        return doAsTransactionService.executeInReadonlyTransaction(user, callable);
    }

    @Override
    public <T> T getInReadonlyTransaction(@Nonnull Supplier<T> supplier) {
        return doAsTransactionService.getInReadonlyTransaction(user, supplier);

    }

    @Override
    public void executeInReadonlyTransaction(@Nonnull Runnable runnable) {
        doAsTransactionService.executeInReadonlyTransaction(user, runnable);
    }

    @Override
    public <T, S> T executeInReadonlyTransaction(S argument, @Nonnull Function<S, T> function) {
        return doAsTransactionService.executeInReadonlyTransaction(user, argument, function);
    }

    @Override
    public <T> void executeInReadonlyTransaction(T argument, @Nonnull Consumer<T> consumer) {
        doAsTransactionService.executeInReadonlyTransaction(user, argument, consumer);

    }

    @Override
    public String toString() {
        return user + "->" + doAsTransactionService;
    }
}
