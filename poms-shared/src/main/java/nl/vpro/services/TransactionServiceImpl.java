package nl.vpro.services;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW , rollbackFor = {Exception.class})
    public <T> T executeInNewTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public <T> T getInNewTransaction(Supplier<T> supplier) {
        return supplier.get();

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeInNewTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T, S> T executeInNewTransaction(S argument, Function<S, T> function) {
        return function.apply(argument);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> void executeInNewTransaction(T argument, Consumer<T> consumer) {
        consumer.accept(argument);

    }


    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T> T executeInReadonlyTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T> T getInReadonlyTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void executeInReadonlyTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T, S> T executeInReadonlyTransaction(S argument, Function<S, T> function) {
        return function.apply(argument);

    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T> void executeInReadonlyTransaction(T argument, Consumer<T> consumer) {
        consumer.accept(argument);
    }
}
