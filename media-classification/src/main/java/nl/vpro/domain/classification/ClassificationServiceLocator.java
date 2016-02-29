package nl.vpro.domain.classification;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class ClassificationServiceLocator  {

    private static final Logger LOG = LoggerFactory.getLogger(ClassificationServiceLocator.class);

    private static ClassificationServiceLocator singleton;

    @Inject
    private Provider<ClassificationService> classificationService = () -> EmptyClassificationService.INSTANCE;

    private ClassificationServiceLocator() {
        singleton = this;
    }

    public static ClassificationService getInstance() {
        if (singleton == null || singleton.classificationService == null || singleton.classificationService.get() == null) {
            LOG.warn("No classification service bean found, returning the empty one");
            return EmptyClassificationService.INSTANCE;
        } else {
            return singleton.classificationService.get();
        }
    }

    public static void setInstance(ClassificationService classificationService) {
        if (singleton == null) {
            new ClassificationServiceLocator();
        }

        if (singleton.classificationService != null && singleton.classificationService.get() != null &&
            ! singleton.classificationService.get().equals(classificationService)
            ) {
            throw new IllegalStateException();
        }
        if (classificationService != null) {
            LOG.info("Using classification service {}", classificationService);
        }
        singleton.classificationService = () -> classificationService;
    }


}
