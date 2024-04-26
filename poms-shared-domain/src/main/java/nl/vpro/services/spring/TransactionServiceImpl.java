package nl.vpro.services.spring;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.functional.ThrowAnyRunnable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import nl.vpro.services.TransactionService;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW , rollbackFor = {Exception.class})
    public <T> T executeInNewTransaction(@NonNull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public <T> T getInNewTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public <T> T getInTransaction(@NonNull Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeInNewTransaction(@NonNull Runnable runnable) {
        runnable.run();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T, S> T executeInNewTransaction(S argument, @NonNull Function<S, T> function) {
        return function.apply(argument);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> void executeInNewTransaction(T argument, @NonNull Consumer<T> consumer) {
        consumer.accept(argument);

    }


    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T> T executeInReadonlyTransaction(@NonNull Callable<T> callable) throws Exception {
        readonly();
        return callable.call();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T> T getInReadonlyTransaction(@NonNull Supplier<T> supplier) {
        readonly();
        return supplier.get();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void executeInReadonlyTransaction(@NonNull ThrowAnyRunnable runnable) {
        readonly();
        runnable.run();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void executeInReadonlyTransaction(@NonNull Runnable runnable) {
        readonly();
        runnable.run();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T, S> T executeInReadonlyTransaction(S argument, @NonNull Function<S, T> function) {
        readonly();
        return function.apply(argument);

    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T> void executeInReadonlyTransaction(T argument, @NonNull Consumer<T> consumer) {
        readonly();
        consumer.accept(argument);
    }

    protected void readonly() {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (org.springframework.transaction.NoTransactionException ignored) {

        }
    }
}
