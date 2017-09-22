package nl.vpro.domain.media.gtaa;

import nl.vpro.w3.rdf.Description;

public class GTAAGeographicName extends AbstractThesaurusItem {

    public static GTAAGeographicName create(Description description) {
        final GTAAGeographicName answer = new GTAAGeographicName();
        fill(description, answer);
        return answer;
    }


}
