package nl.vpro.domain.media.gtaa;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.w3.rdf.Description;
import nl.vpro.w3.rdf.ResourceElement;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class ThesaurusObjects {


    public static ThesaurusObject toThesaurusObject(Description d) {
        switch(toType(d.getInScheme())) {
            case "Persoon":
                return GTAAPerson.create(d);
            case "GeogragrafischeNaam":
                return GTAATopic.create(d);
            default:
                return ThesaurusItem.create(d);
        }
    }

    private static String toType(ResourceElement inScheme) {
        return StringUtils.substringAfterLast(inScheme.getResource(), "/");
    }


}
