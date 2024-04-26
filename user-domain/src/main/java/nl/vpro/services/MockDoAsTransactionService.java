package nl.vpro.services;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.functional.ThrowAnyRunnable;

import nl.vpro.domain.user.Trusted;

/**
 * @author Michiel Meeuwissen
 */
public class MockDoAsTransactionService extends MockTransactionService implements DoAsTransactionService {
    @Override
    @SneakyThrows
    public <T> T executeInNewTransaction(@NonNull Trusted user, @NonNull Callable<T> callable) {
        return callable.call();
    }

    @Override
    public <T, S> T executeInNewTransaction(@NonNull Trusted user, S argument, @NonNull Function<S, T> function) {
        return function.apply(argument);
    }

    @Override
    public void executeInNewTransaction(@NonNull Trusted user, @NonNull Runnable runnable) {
        runnable.run();
    }

    @Override
    @SneakyThrows
    public <T> T executeInReadonlyTransaction(@NonNull Trusted user, @NonNull Callable<T> callable) {
        return callable.call();
    }

    @Override
    public <T, S> T executeInReadonlyTransaction(@NonNull Trusted user, S argument, @NonNull Function<S, T> function) {
        return function.apply(argument);
    }

    @Override
    public void executeInReadonlyTransaction(@NonNull Trusted user, @NonNull Runnable runnable) {
        runnable.run();
    }

    @Override
    public void executeInReadonlyTransaction(@NonNull Trusted user, @NonNull ThrowAnyRunnable runnable) {
        runnable.run();
    }

    @Override
    public <T> void executeInReadonlyTransaction(@NonNull Trusted user, T argument, @NonNull Consumer<T> consumer) {
        consumer.accept(argument);
    }

    @Override
    public <T> T getInNewTransaction(@NonNull Trusted user, @NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    public <T> T getInTransaction(@NonNull Trusted user, @NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    public <T> T getInReadonlyTransaction(@NonNull Trusted user, @NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    public <T> void executeInNewTransaction(@NonNull Trusted user, T argument, @NonNull Consumer<T> consumer) {
        consumer.accept(argument);
    }
}
