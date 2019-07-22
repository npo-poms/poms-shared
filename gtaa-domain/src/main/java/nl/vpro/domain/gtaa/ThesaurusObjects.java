package nl.vpro.domain.gtaa;

import java.util.Optional;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class ThesaurusObjects {
    private static final String SCHEME_URI = "http://data.beeldengeluid.nl/gtaa/";

    static {

    }

    public static ThesaurusObject toThesaurusObject(Description d) {
        Optional<Scheme> scheme = Scheme.ofUrl(d.getInScheme().getResource());
        if (scheme.isPresent()) {
            switch(scheme.get()) {
                case person:
                    return GTAAPerson.create(d);
                case topic:
                    return GTAATopic.create(d);
                case genre:
                    return GTAAGenre.create(d);
                case name:
                    return GTAAName.create(d);
                case geographicname:
                    return GTAAGeographicName.create(d);
                case maker:
                    return GTAAMaker.create(d);
                case classification:
                case topicbandg:
                default:
                    return ThesaurusItem.create(d);

            }
        } else {
            return ThesaurusItem.create(d);
        }
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
