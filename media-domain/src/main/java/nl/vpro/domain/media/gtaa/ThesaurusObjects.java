package nl.vpro.domain.media.gtaa;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class ThesaurusObjects {
    private static final String SCHEME_URI = "http://data.beeldengeluid.nl/gtaa/";

    public static ThesaurusObject toThesaurusObject(Description d) {
        switch(d.getInScheme().getResource()) {
            case Schemes.PERSOONSNAMEN:
                return GTAAPerson.create(d);
            case Schemes.ONDERWERPEN:
                return GTAATopic.create(d);
            case Schemes.GENRE:
                return GTAAGenre.create(d);
            case Schemes.NAMEN:
                return GTAAName.create(d);
            case Schemes.GEOGRAFISCHENAMEN:
                return GTAAGeographicName.create(d);
            case Schemes.MAKER:
                return GTAAMaker.create(d);
            default:
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


    public static String toScheme(ThesaurusObject d) {
        return toScheme((Object) d);
    }

    public static String toScheme(NewThesaurusObject<?> d) {
        return toScheme((Object) d);
    }
    private static String toScheme(Object d) {
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
