package nl.vpro.domain.gtaa;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonSubTypes;

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
                case PERSOONSNAMEN:
                    return GTAAPerson.create(d);
                case ONDERWERPEN:
                    return GTAATopic.create(d);
                case GENRE:
                    return GTAAGenre.create(d);
                case NAMEN:
                    return GTAAName.create(d);
                case GEOGRAFISCHENAMEN:
                    return GTAAGeographicName.create(d);
                case MAKER:
                    return GTAAMaker.create(d);
                case CLASSIFICATIE:
                case ONDERWERPENBENG:
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
        return toScheme((Object) d);
    }

    public static Scheme toScheme(NewThesaurusObject<?> d) {
        return toScheme((Object) d);
    }
    private static Scheme toScheme(Object d) {
        return d.getClass().getAnnotation(GTAAScheme.class).value();
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
