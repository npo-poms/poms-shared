package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import nl.vpro.domain.user.Trusted;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class MockDoAsTransactionService extends MockTransactionService implements DoAsTransactionService {
    @Override
    public <T> T executeInNewTransaction(@Nonnull Trusted user, @Nonnull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    public <T, S> T executeInNewTransaction(@Nonnull Trusted user, S argument, @Nonnull Function<S, T> function) {
        return function.apply(argument);
    }

    @Override
    public void executeInNewTransaction(@Nonnull Trusted user, @Nonnull Runnable runnable) {
        runnable.run();
    }

    @Override
    public void executeInNewTransaction(@Nonnull String user, @Nonnull Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T> T executeInReadonlyTransaction(@Nonnull Trusted user, @Nonnull Callable<T> callable) throws Exception {
        return callable.call();

    }

    @Override
    public <T, S> T executeInReadonlyTransaction(@Nonnull Trusted user, S argument, @Nonnull Function<S, T> function) {
        return function.apply(argument);

    }

    @Override
    public void executeInReadonlyTransaction(@Nonnull Trusted user, @Nonnull Runnable runnable) {
        runnable.run();

    }

    @Override
    public <T> void executeInReadonlyTransaction(@Nonnull Trusted user, T argument, @Nonnull Consumer<T> consumer) {
        consumer.accept(argument);

    }

    @Override
    public <T> T getInNewTransaction(@Nonnull Trusted user, @Nonnull Supplier<T> supplier) {
        return supplier.get();

    }

    @Override
    public <T> T getInReadonlyTransaction(@Nonnull Trusted user, @Nonnull Supplier<T> supplier) {
        return supplier.get();

    }

    @Override
    public <T> void executeInNewTransaction(@Nonnull Trusted user, T argument, @Nonnull Consumer<T> consumer) {
        consumer.accept(argument);

    }
}
