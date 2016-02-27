package nl.vpro.domain.classification;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class ClassificationServiceLocator  {

    private static final Logger LOG = LoggerFactory.getLogger(ClassificationServiceLocator.class);

    @Inject
    private static ClassificationService classificationService;
    
    private ClassificationServiceLocator() {
    }

    public static ClassificationService getInstance() {
        if (classificationService == null) {
            LOG.warn("No classification service bean found, returning the empty one");
            return EmptyClassificationService.INSTANCE;
        } else {
            return classificationService;
        }
    }

    public static void setInstance(ClassificationService classificationService) {
        if (ClassificationServiceLocator.classificationService != null &&
            ! ClassificationServiceLocator.classificationService.equals(classificationService)
            ) {
            throw new IllegalStateException();
        }
        if (classificationService != null) {
            LOG.info("Using classification service {}", classificationService);
        }
        ClassificationServiceLocator.classificationService = classificationService;
    }

 
}
