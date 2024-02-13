package nl.vpro.domain.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.TextMatcherListJson;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "textMatcherListType")
@JsonSerialize(using = TextMatcherListJson.Serializer.class)
@JsonDeserialize(using = TextMatcherListJson.Deserializer.class)
public class TextMatcherList extends AbstractTextMatcherList<TextMatcher, StandardMatchType> {

    public static TextMatcherList must(TextMatcher... values) {
        return new TextMatcherList(Match.MUST, values);
    }

    public static TextMatcherList must(Stream<TextMatcher> values) {
        return new TextMatcherList(values.collect(Collectors.toList()), Match.MUST);
    }

    public static TextMatcherList should(TextMatcher... values) {
        return new TextMatcherList(Match.SHOULD, values);
    }

    public static TextMatcherList should(Stream<TextMatcher> values) {
        return new TextMatcherList(values.collect(Collectors.toList()), Match.SHOULD);
    }

    public static TextMatcherList not(TextMatcher... values) {
        return new TextMatcherList(Match.NOT, values);
    }

    public static TextMatcherList not(Stream<TextMatcher> values) {
        return new TextMatcherList(values.collect(Collectors.toList()), Match.NOT);
    }

    public TextMatcherList() {
        super();
    }

    public TextMatcherList(List<TextMatcher> values, Match match) {
        super(match, values);
        this.matchers = values;
    }

    public TextMatcherList(TextMatcher... values) {
        super(DEFAULT_MATCH, Arrays.asList(values));
    }


    public TextMatcherList(Match match, TextMatcher... values) {
        super(match, Arrays.asList(values));
    }


    @Override
    @XmlElement(name = "matcher")
    public List<TextMatcher> getMatchers() {
        return super.getMatchers();
    }

    @Override
    public void setMatchers(List<TextMatcher> matchers) {
        super.setMatchers(matchers);
    }


}
