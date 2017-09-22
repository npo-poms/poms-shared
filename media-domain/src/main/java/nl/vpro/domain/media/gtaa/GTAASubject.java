package nl.vpro.domain.media.gtaa;

import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class GTAASubject extends AbstractThesaurusItem {


    public static GTAASubject create(Description description) {
        final GTAASubject answer = new GTAASubject();
        fill(description, answer);
        return answer;
    }

}
