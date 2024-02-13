package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlType(name = "integerRangeMatcherType", propOrder = {"begin", "end"})
public class IntegerRangeMatcher extends SimpleRangeMatcher<Integer>   {

    @XmlElement
    @Getter
    @Setter
    private Integer begin;
    @XmlElement
    @Getter
    @Setter
    private Integer end;

    public IntegerRangeMatcher() {
    }

    public IntegerRangeMatcher(Integer begin, Integer end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected boolean defaultIncludeEnd() {
        return false;

    }
}
