package nl.vpro.domain.classification;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@Slf4j
public class ClassificationServiceLocator  {


    private static ClassificationServiceLocator singleton;

    private static boolean warned = false;

    private static List<String> terms;

    @Inject
    private Provider<ClassificationService> classificationService = () -> EmptyClassificationService.INSTANCE;

    private ClassificationServiceLocator() {
        singleton = this;
        ServiceLoader.load(ClassificationService.class).iterator().forEachRemaining(c -> {
            // TODO load this stuff a bit more easily
            }
        );
    }
    private ClassificationServiceLocator(ClassificationService classificationService) {
        this();
        this.classificationService = () -> classificationService;
    }

    public static ClassificationService getInstance() {
        if (singleton == null || singleton.classificationService == null || singleton.classificationService.get() == null) {
            if (! warned) {
                log.warn("No classification service bean found, returning the empty one");
                warned = true;
            }
            return EmptyClassificationService.INSTANCE;
        } else {
            return singleton.classificationService.get();
        }
    }

    public static void setInstance(ClassificationService classificationService) {
        if (singleton == null) {
            new ClassificationServiceLocator(classificationService);
        } else {
            if (singleton.classificationService.get() != EmptyClassificationService.INSTANCE
                && !singleton.classificationService.get().equals(classificationService)
            ) {
                throw new IllegalStateException();
            }
            if (classificationService != null) {
                log.info("Using classification service {} with {} terms", classificationService, classificationService.getClassificationScheme().getTerms().size());
            }
            singleton.classificationService = () -> classificationService;
        }
        warned = false;
    }

    public static List<String> getTerms() {
        if (terms == null) {
            terms = getInstance()
                .values()
                .stream()
                .map(Term::getTermId)
                .collect(Collectors.toList());
        }
        return terms;

    }


}
