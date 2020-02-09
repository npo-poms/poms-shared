package nl.vpro.domain.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

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
public class DurationRangeMatcherList extends MatcherList<DurationRangeMatcher> implements Predicate<Duration> {

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


    @Override
    public boolean test(@Nullable Duration input) {
        if (input == null) return true;

        switch (match) {

            case MUST: {
                for (DurationRangeMatcher matcher : this) {
                    if (!matcher.test(input)) {
                        return false;
                    }
                }
                return true;
            }
            case NOT: {
                for (DurationRangeMatcher matcher : this) {
                    if (matcher.test(input)) {
                        return false;
                    }
                }
                return true;
            }
            case SHOULD:
            default: {
                for (DurationRangeMatcher matcher : this) {
                    if (matcher.test(input)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
