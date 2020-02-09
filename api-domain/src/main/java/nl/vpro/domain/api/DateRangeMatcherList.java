package nl.vpro.domain.api;

import lombok.Singular;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.DateRangeMatcherListJson;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dateRangeMatcherListType")
@JsonSerialize(using = DateRangeMatcherListJson.Serializer.class)
@JsonDeserialize(using = DateRangeMatcherListJson.Deserializer.class)
public class DateRangeMatcherList extends MatcherList<DateRangeMatcher> implements Predicate<Instant> {

    @XmlElement(name = "matcher")
    @Valid
    protected List<DateRangeMatcher> matchers = new ArrayList<>();

    public DateRangeMatcherList() {
    }

    @lombok.Builder
    public DateRangeMatcherList(@Singular  List<DateRangeMatcher> values, Match match) {
        super(match);
        this.matchers = values;
    }

    public DateRangeMatcherList(DateRangeMatcher... values) {
        super(DEFAULT_MATCH);
        this.matchers = Arrays.asList(values);
    }


    public DateRangeMatcherList(Match match, DateRangeMatcher... values) {
        super(match);
        this.matchers = Arrays.asList(values);
    }

    @Override
    public List<DateRangeMatcher> asList() {
        return matchers;
    }


    @Override
    public boolean test(@Nullable Instant input) {
        if (input == null) return true;

        switch (match) {

            case MUST: {
                for (DateRangeMatcher matcher : this) {
                    if (!matcher.test(input)) {
                        return false;
                    }
                }
                return true;
            }
            case NOT: {
                for (DateRangeMatcher matcher : this) {
                    if (matcher.test(input)) {
                        return false;
                    }
                }
                return true;
            }
            case SHOULD:
            default: {
                for (DateRangeMatcher matcher : this) {
                    if (matcher.test(input)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
