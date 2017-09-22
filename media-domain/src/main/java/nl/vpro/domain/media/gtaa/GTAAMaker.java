package nl.vpro.domain.media.gtaa;

import nl.vpro.w3.rdf.Description;

public class GTAAMaker extends AbstractThesaurusItem {

    public static GTAAMaker create(Description description) {
        final GTAAMaker answer = new GTAAMaker();
        fill(description, answer);
        return answer;
    }


}
