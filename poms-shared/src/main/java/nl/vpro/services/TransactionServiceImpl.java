package nl.vpro.services;

import java.util.concurrent.Callable;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 100, rollbackFor = {Throwable.class})
    public <T> T executeInNewTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeInNewTransaction(Runnable runnable) {
        runnable.run();
    }


    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public <T> T executeInReadonlyTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }


    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void executeInReadonlyTransaction(Runnable runnable) {
        runnable.run();
    }
}
