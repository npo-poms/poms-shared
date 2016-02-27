package nl.vpro.domain.classification;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public interface ClassificationService {
    
    static ClassificationService getInstance() {
        try {
            // TODO
            return (ClassificationService) Class.forName("nl.vpro.domain.classification.ClassificationServiceLocator").getMethod("getInstance").invoke(null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    static void setInstance(ClassificationService classificationService) {
        try {
            // TODO
            Class.forName("nl.vpro.domain.classification.ClassificationServiceLocator").getMethod("setInstance").invoke(null, classificationService);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    /**
     * Returns the Term with the given id.
     * @param termId
     * @throws TermNotFoundException if no such term
     */

    Term getTerm(String termId) throws TermNotFoundException;

    List<Term> getTermsByReference(String reference);

    boolean hasTerm(String termId);

    Collection<Term> values();

    Collection<Term> valuesOf(String termId);

    ClassificationScheme getClassificationScheme();

    Date getLastModified();

}
