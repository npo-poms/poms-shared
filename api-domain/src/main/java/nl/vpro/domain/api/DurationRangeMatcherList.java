package nl.vpro.domain.api;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.DurationRangeMatcherListJson;
import nl.vpro.domain.api.media.DurationRangeMatcher;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "durationRangeMatcherListType")
@JsonSerialize(using = DurationRangeMatcherListJson.Serializer.class)
@JsonDeserialize(using = DurationRangeMatcherListJson.Deserializer.class)
public class DurationRangeMatcherList extends MatcherList<Duration, DurationRangeMatcher> implements Predicate<Duration> {

    @XmlElement(name = "matcher")
    @Valid
    protected List<DurationRangeMatcher> matchers = new ArrayList<>();

    public DurationRangeMatcherList() {
    }

    public DurationRangeMatcherList(List<DurationRangeMatcher> values, Match match) {
        super(match);
        this.matchers = values;
    }

    public DurationRangeMatcherList(DurationRangeMatcher... values) {
        super(DEFAULT_MATCH);
        this.matchers = Arrays.asList(values);
    }


    public DurationRangeMatcherList(Match match, DurationRangeMatcher... values) {
        super(match);
        this.matchers = Arrays.asList(values);
    }

    @Override
    public List<DurationRangeMatcher> asList() {
        return matchers;
    }

}
