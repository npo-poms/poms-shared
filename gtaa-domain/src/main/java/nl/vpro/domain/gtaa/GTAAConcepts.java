package nl.vpro.domain.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class GTAAConcepts {
    private static final String SCHEME_URI = "http://data.beeldengeluid.nl/gtaa/";

    static {

    }

    public static GTAAConcept toConcept(Description d) {
        return (GTAAConcept) Scheme.ofUrl(d.getInScheme().getResource()).map(s -> {
            try {
                return s.getImplementation().getMethod("create", Description.class).invoke(null, d);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }).orElseThrow(() -> new IllegalArgumentException("Not convertible " + d));
    }


    public static GTAAConcept toConcept(GTAANewGenericConcept thesaurusObject) {
        Description description = Description.builder()
                .prefLabel(
                        Label.builder()
                                .value(thesaurusObject.getName())
                                .build()
                ).scopeNote(thesaurusObject.getScopeNotesAsLabel())
                .inScheme(SCHEME_URI + thesaurusObject.getObjectType())
                .build();
        return toConcept(description);
    }


    public static Scheme toScheme(GTAAConcept d) {
        return toScheme((Object) d).orElseThrow(() -> new IllegalArgumentException("" + d + " has no scheme"));
    }

     private static Optional<Scheme> toScheme(Object d) {
        GTAAScheme annotation = d.getClass().getAnnotation(GTAAScheme.class);
        if (annotation != null) {
            return Optional.of(annotation.value());
        } else {
            return Optional.empty();
        }
    }


    private static Description toDescription(GTAANewGenericConcept thesaurusObject) {
        return Description.builder()
                .prefLabel(
                        Label.builder()
                                .value(thesaurusObject.getName())
                                .build()
                ).scopeNote(thesaurusObject.getScopeNotesAsLabel())
                .inScheme(SCHEME_URI + thesaurusObject.getObjectType())
                .build();
    }
}
