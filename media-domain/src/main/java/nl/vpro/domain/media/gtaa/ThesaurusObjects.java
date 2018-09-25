package nl.vpro.domain.media.gtaa;

import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;

import java.util.Arrays;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class ThesaurusObjects {


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
        switch(thesaurusObject.getObjectType()) {
            case "person":
                return GTAAPerson.create(toDescription(thesaurusObject));
            case "topic":
                return GTAATopic.create(toDescription(thesaurusObject));
            case "genre":
                return GTAAGenre.create(toDescription(thesaurusObject));
            case "name":
                return GTAAName.create(toDescription(thesaurusObject));
            case "geofraphicname":
                return GTAAGeographicName.create(toDescription(thesaurusObject));
            case "maker":
                return GTAAMaker.create(toDescription(thesaurusObject));
            default:
                return ThesaurusItem.create(toDescription(thesaurusObject));
        }
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
                ).scopeNote(Arrays.asList(Label.forValue(thesaurusObject.getValue())))
                .build();
    }
}
