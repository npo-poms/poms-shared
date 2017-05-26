package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlType(name = "integerRangeMatcherType", propOrder = {"begin", "end"})
public class IntegerRangeMatcher extends RangeMatcher<Integer> implements Predicate<Integer> {

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


    @Override
    public boolean test(Integer integer) {
        return super.testComparable(integer);

    }
}
