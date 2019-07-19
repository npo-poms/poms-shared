package nl.vpro.domain.api.thesaurus;

import lombok.Builder;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import nl.vpro.domain.gtaa.GTAAPerson;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@XmlSeeAlso({GTAAPerson.class})
@XmlRootElement
public class PersonResult extends ThesaurusResult<GTAAPerson> {

    protected PersonResult() {

    }

    @Builder
    public PersonResult(List<GTAAPerson> list, Integer max) {
        super(list, max);
    }
}
