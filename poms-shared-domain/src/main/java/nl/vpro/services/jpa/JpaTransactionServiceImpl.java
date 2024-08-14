package nl.vpro.services.jpa;

import java.util.concurrent.Callable;
import java.util.function.*;

import jakarta.transaction.Transactional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;

import nl.vpro.services.TransactionService;

import static jakarta.transaction.Transactional.TxType.*;

@Service("jpaTransactionService")
public class JpaTransactionServiceImpl implements TransactionService {

    @Override
    @Transactional(value = REQUIRES_NEW, rollbackOn = {Exception.class})
    public <T> T executeInNewTransaction(@NonNull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(value = REQUIRED, rollbackOn = {Exception.class})
    public <T> T executeInTransaction(@NonNull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(value = REQUIRES_NEW, rollbackOn = {Exception.class})
    public <T> T getInNewTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    @Transactional(value = REQUIRED, rollbackOn = {Exception.class})
    public <T> T getInTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    @Transactional(REQUIRES_NEW)
    public void executeInNewTransaction(@NonNull Runnable runnable) {
        runnable.run();
    }


    @Override
    @Transactional(REQUIRED)
    public void executeInTransaction(@NonNull Runnable runnable) {
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
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public <T> T executeInReadonlyTransaction(@NonNull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public <T> T getInReadonlyTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public void executeInReadonlyTransaction(@NonNull Runnable runnable) {
        runnable.run();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public <T, S> T executeInReadonlyTransaction(S argument, @NonNull Function<S, T> function) {
        return function.apply(argument);

    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public <T> void executeInReadonlyTransaction(T argument, @NonNull Consumer<T> consumer) {
        consumer.accept(argument);
    }

    @Override
    @Transactional(NEVER)
    public void ensureNoTransaction() {

    }

}
