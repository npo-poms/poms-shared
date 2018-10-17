package nl.vpro.domain.media.gtaa;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

import java.util.Arrays;

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
                ).scopeNote(Arrays.asList(Label.forValue(thesaurusObject.getNote())))
                .inScheme(SCHEME_URI + thesaurusObject.getObjectType())
                .build();
        return toThesaurusObject(description);
    }


    public static String toScheme(ThesaurusObject d) {
        switch(d.getClass().getSimpleName()) {
            case "GTAAPerson":
                return Schemes.PERSOONSNAMEN;
            case "GTAATopic":
                return Schemes.ONDERWERPEN;
            case "GTAAGenre":
                return  Schemes.GENRE;
            case "GTAAName":
                return Schemes.NAMEN;
            case "GTAAGeographicName":
                return Schemes.GEOGRAFISCHENAMEN;
            case "GTAAMaker":
                return Schemes.MAKER;
            default:
                return "";
        }
    }

    private static Description toDescription(GTAANewThesaurusObject thesaurusObject) {
        return Description.builder()
                .prefLabel(
                        Label.builder()
                                .value(thesaurusObject.getValue())
                                .build()
                ).scopeNote(Arrays.asList(Label.forValue(thesaurusObject.getNote())))
                .inScheme(SCHEME_URI + thesaurusObject.getObjectType())
                .build();
    }
}
