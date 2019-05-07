package nl.vpro.domain.media.gtaa;

import nl.vpro.w3.rdf.Description;

@GTAAScheme(Schemes.GENRE)
public class GTAAGenre extends AbstractThesaurusItem {


    public static GTAAGenre create(Description description) {
        final GTAAGenre answer = new GTAAGenre();
        fill(description, answer);
        return answer;
    }

}
