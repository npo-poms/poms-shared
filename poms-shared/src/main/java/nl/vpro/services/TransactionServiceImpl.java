package nl.vpro.services;

import java.util.concurrent.Callable;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T executeInNewTransaction(Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeInNewTransaction(Runnable runnable) {
        runnable.run();
    }
}
