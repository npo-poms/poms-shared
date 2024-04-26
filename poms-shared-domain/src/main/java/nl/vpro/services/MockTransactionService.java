package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.functional.ThrowAnyRunnable;


public class MockTransactionService implements TransactionService {

    @Override
    public <T> T executeInNewTransaction(@NonNull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    public <T> T getInNewTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    public <T> T getInTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();
    }


    @Override
    public void executeInNewTransaction(@NonNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T, S> T executeInNewTransaction(S argument, @NonNull Function<S, T> function) {
        return function.apply(argument);
    }

    @Override
    public <T> void executeInNewTransaction(T argument, @NonNull Consumer<T> consumer) {
        consumer.accept(argument);
    }


    @Override
    public <T> T executeInReadonlyTransaction(@NonNull Callable<T> callable) throws Exception {
        readonly();
        return callable.call();
    }

    @Override
    public <T> T getInReadonlyTransaction(@NonNull Supplier<T> supplier) {
        readonly();
        return supplier.get();
    }

    @Override
    public void executeInReadonlyTransaction(@NonNull ThrowAnyRunnable runnable) {
        readonly();
        runnable.run();
    }

    @Override
    public void executeInReadonlyTransaction(@NonNull Runnable runnable) {
        readonly();
        runnable.run();
    }

    @Override
    public <T, S> T executeInReadonlyTransaction(S argument, @NonNull Function<S, T> function) {
        readonly();
        return function.apply(argument);
    }

    @Override
    public <T> void executeInReadonlyTransaction(T argument, @NonNull Consumer<T> consumer) {
        readonly();
        consumer.accept(argument);
    }

    protected void readonly() {
    }
}
