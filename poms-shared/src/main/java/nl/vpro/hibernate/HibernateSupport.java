package nl.vpro.hibernate;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class HibernateSupport {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private PlatformTransactionManager txManager;

//    private TransactionTemplate transactionTemplate;

    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected FullTextSession getFullTextSession() {
        return Search.getFullTextSession(getSession());
    }

    protected TransactionTemplate getReadOnlyTransactionTemplate() {
        final TransactionTemplate transactionTemplate = getTransactionTemplate();
        transactionTemplate.setReadOnly(true);
        return transactionTemplate;
    }

    protected TransactionTemplate getTransactionTemplate() {
//        if(transactionTemplate == null) {
//            this.transactionTemplate = new TransactionTemplate(transactionManager);
//        }

        return new TransactionTemplate(txManager);
    }
}
