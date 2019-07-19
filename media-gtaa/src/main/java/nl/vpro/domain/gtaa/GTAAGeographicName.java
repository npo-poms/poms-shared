package nl.vpro.domain.gtaa;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.GEOGRAFISCHENAMEN)
public class GTAAGeographicName extends AbstractThesaurusItem {

    public static GTAAGeographicName create(Description description) {
        final GTAAGeographicName answer = new GTAAGeographicName();
        fill(description, answer);
        return answer;
    }


}
