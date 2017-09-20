package nl.vpro.domain.media.gtaa;

import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class GTAATopic extends AbstractThesaurusItem {


    public static GTAATopic create(Description description) {
        final GTAATopic answer = new GTAATopic();
        fill(description, answer);
        return answer;
    }

}
