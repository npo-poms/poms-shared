package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlType(name = "integerRangeMatcherType", propOrder = {"begin", "end"})
public class IntegerRangeMatcher extends RangeMatcher<Integer> {

    public IntegerRangeMatcher() {


    }

    public IntegerRangeMatcher(Integer begin, Integer end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    @XmlElement
    public Integer getBegin() {
        return begin;
    }

    @Override
    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    @Override
    @XmlElement
    public Integer getEnd() {
        return end;
    }

    @Override
    public void setEnd(Integer end) {
        this.end = end;
    }
}
