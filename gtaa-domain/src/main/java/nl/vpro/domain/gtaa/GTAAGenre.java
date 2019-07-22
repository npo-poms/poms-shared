package nl.vpro.domain.gtaa;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Scheme.genre)
public class GTAAGenre extends AbstractThesaurusItem {


    public static GTAAGenre create(Description description) {
        final GTAAGenre answer = new GTAAGenre();
        fill(description, answer);
        return answer;
    }

}
