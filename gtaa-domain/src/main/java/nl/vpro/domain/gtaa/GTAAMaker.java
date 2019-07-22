package nl.vpro.domain.gtaa;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.maker)
public class GTAAMaker extends AbstractThesaurusItem {

    public static GTAAMaker create(Description description) {
        final GTAAMaker answer = new GTAAMaker();
        fill(description, answer);
        return answer;
    }


}
