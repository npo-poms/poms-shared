package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;


public class MockTransactionService implements TransactionService {

    @Override
    public <T> T executeInNewTransaction(@Nonnull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    public <T> T getInNewTransaction(@Nonnull Supplier<T> supplier) {
        return supplier.get();

    }

    @Override
    public void executeInNewTransaction(@Nonnull Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T, S> T executeInNewTransaction(S argument, @Nonnull Function<S, T> function) {
        return function.apply(argument);
    }

    @Override
    public <T> void executeInNewTransaction(T argument, @Nonnull Consumer<T> consumer) {
        consumer.accept(argument);

    }


    @Override
    public <T> T executeInReadonlyTransaction(@Nonnull Callable<T> callable) throws Exception {
        readonly();
        return callable.call();
    }

    @Override
    public <T> T getInReadonlyTransaction(@Nonnull Supplier<T> supplier) {
        readonly();
        return supplier.get();
    }

    @Override
    public void executeInReadonlyTransaction(@Nonnull Runnable runnable) {
        readonly();
        runnable.run();
    }

    @Override
    public <T, S> T executeInReadonlyTransaction(S argument, @Nonnull Function<S, T> function) {
        readonly();
        return function.apply(argument);

    }

    @Override
    public <T> void executeInReadonlyTransaction(T argument, @Nonnull Consumer<T> consumer) {
        readonly();
        consumer.accept(argument);
    }

    protected void readonly() {

    }
}
