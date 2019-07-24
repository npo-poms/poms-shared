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
public class ThesaurusObjects {
    private static final String SCHEME_URI = "http://data.beeldengeluid.nl/gtaa/";

    static {

    }

    public static ThesaurusObject toThesaurusObject(Description d) {
        return (ThesaurusObject) Scheme.ofUrl(d.getInScheme().getResource()).map(s -> {
            try {
                return s.getImplementation().getMethod("create", Description.class).invoke(null, d);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }).orElseThrow(() -> new IllegalStateException("Not convertible " + d));
    }


    public static ThesaurusObject toThesaurusObject(GTAANewThesaurusObject thesaurusObject) {
        Description description = Description.builder()
                .prefLabel(
                        Label.builder()
                                .value(thesaurusObject.getValue())
                                .build()
                ).scopeNote(thesaurusObject.getNotesAsLabel())
                .inScheme(SCHEME_URI + thesaurusObject.getObjectType())
                .build();
        return toThesaurusObject(description);
    }


    public static Scheme toScheme(ThesaurusObject d) {
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


    private static Description toDescription(GTAANewThesaurusObject thesaurusObject) {
        return Description.builder()
                .prefLabel(
                        Label.builder()
                                .value(thesaurusObject.getValue())
                                .build()
                ).scopeNote(thesaurusObject.getNotesAsLabel())
                .inScheme(SCHEME_URI + thesaurusObject.getObjectType())
                .build();
    }
}
