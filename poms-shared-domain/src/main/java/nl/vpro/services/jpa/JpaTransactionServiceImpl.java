package nl.vpro.services.jpa;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.transaction.Transactional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;

import nl.vpro.services.TransactionService;

import static javax.transaction.Transactional.TxType.*;

@Service("jpaTransactionService")
public class JpaTransactionServiceImpl implements TransactionService {

    @Override
    @Transactional(value = REQUIRES_NEW, rollbackOn = {Exception.class})
    public <T> T executeInNewTransaction(@NonNull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(value = REQUIRES_NEW, rollbackOn = {Exception.class})
    public <T> T getInNewTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();

    }

    @Override
    @Transactional(REQUIRES_NEW)
    public void executeInNewTransaction(@NonNull Runnable runnable) {
        runnable.run();
    }

    @Override
    @Transactional(REQUIRES_NEW)
    public <T, S> T executeInNewTransaction(S argument, @NonNull Function<S, T> function) {
        return function.apply(argument);
    }

    @Override
    @Transactional(REQUIRES_NEW)
    public <T> void executeInNewTransaction(T argument, @NonNull Consumer<T> consumer) {
        consumer.accept(argument);

    }


    @Override
    @Transactional(REQUIRES_NEW) // READONLY?
    public <T> T executeInReadonlyTransaction(@NonNull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(REQUIRES_NEW) // READONLY
    public <T> T getInReadonlyTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    @Transactional(REQUIRES_NEW) // READONLY
    public void executeInReadonlyTransaction(@NonNull Runnable runnable) {
        runnable.run();
    }

    @Override
    @Transactional(REQUIRES_NEW) // READONLY
    public <T, S> T executeInReadonlyTransaction(S argument, @NonNull Function<S, T> function) {
        return function.apply(argument);

    }

    @Override
    @Transactional(REQUIRES_NEW) // READONLY
    public <T> void executeInReadonlyTransaction(T argument, @NonNull Consumer<T> consumer) {
        consumer.accept(argument);
    }
}
