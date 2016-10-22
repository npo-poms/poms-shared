package nl.vpro.hibernate;


import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class HibernateSupport {

    @Inject
    private SessionFactory sessionFactory;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    protected EntityManager entityManager;


    @Inject
    private PlatformTransactionManager txManager;

//    private TransactionTemplate transactionTemplate;

    @Deprecated
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Deprecated
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }


    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Deprecated
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
