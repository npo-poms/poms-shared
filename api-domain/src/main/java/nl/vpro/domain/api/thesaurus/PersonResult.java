package nl.vpro.domain.api.thesaurus;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

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

    @lombok.Builder
    public PersonResult(List<GTAAPerson> list, Integer max) {
        super(list, max);
    }
}
