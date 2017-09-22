package nl.vpro.domain.media.gtaa;

import nl.vpro.w3.rdf.Description;

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
                return GTAASubject.create(d);
            case Schemes.GENRE:
                return GTAASubject.create(d);
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
}
