package nl.vpro.domain.gtaa;

import lombok.Getter;

import nl.vpro.w3.rdf.Description;

public class ThesaurusItem extends AbstractThesaurusItem {


    @Getter
    private String type;


    public static ThesaurusItem create(Description description) {
        final ThesaurusItem answer = new ThesaurusItem();
        answer.type = description.getType() == null ? null : description.getType().getResource();
        fill(description, answer);
        return answer;
    }
}
