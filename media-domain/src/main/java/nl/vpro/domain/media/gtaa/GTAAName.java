package nl.vpro.domain.media.gtaa;

import nl.vpro.w3.rdf.Description;

public class GTAAName extends AbstractThesaurusItem {

    public static GTAAName create(Description description) {
        final GTAAName answer = new GTAAName();
        fill(description, answer);
        return answer;
    }


}
