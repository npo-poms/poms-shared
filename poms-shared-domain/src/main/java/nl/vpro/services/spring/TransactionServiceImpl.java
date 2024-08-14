package nl.vpro.services.spring;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.function.*;

import jakarta.transaction.Transactional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import nl.vpro.services.TransactionService;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

@Slf4j
@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {



    @Override
    @Transactional(value = REQUIRES_NEW , rollbackOn = {Exception.class})
    public <T> T executeInNewTransaction(@NonNull Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(value = REQUIRED , rollbackOn = {Exception.class})
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
    @Transactional(REQUIRES_NEW)
    public <T> T executeInReadonlyTransaction(@NonNull Callable<T> callable) throws Exception {
        readonly();
        return callable.call();
    }

    @Override
    @Transactional(REQUIRES_NEW)
    public <T> T getInReadonlyTransaction(@NonNull Supplier<T> supplier) {
        readonly();
        return supplier.get();
    }

    @Override
    @Transactional(REQUIRES_NEW)
    public void executeInReadonlyTransaction(@NonNull Runnable runnable) {
        //assert entityManagerFactory.unwrap(org.hibernate.SessionFactory.class).getCurrentSession() != null;
        readonly();
        runnable.run();
    }


    @Override
    @Transactional(REQUIRES_NEW)
    public <T, S> T executeInReadonlyTransaction(S argument, @NonNull Function<S, T> function) {
        readonly();
        return function.apply(argument);

    }

    @Override
    @Transactional
    public <T> void executeInReadonlyTransaction(T argument, @NonNull Consumer<T> consumer) {
        readonly();
        consumer.accept(argument);
    }

    protected void readonly() {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (org.springframework.transaction.NoTransactionException ignored) {
            log.debug("No transaction");
        }
    }
}
