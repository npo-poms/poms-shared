package nl.vpro.domain.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Duration;
import java.util.regex.Pattern;

import jakarta.xml.bind.annotation.XmlValue;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Getter
@EqualsAndHashCode
abstract class ParsedInterval<T extends Comparable<T>> {

    static final String TEMPORAL_AMOUNT_INTERVAL = "(?:\\d+)?\\s*(?:YEAR|MONTH|WEEK|DAY|HOUR|MINUTE)S?";

    static final Pattern PATTERN = Pattern.compile("(\\d+)?\\s*(YEAR|MONTH|WEEK|DAY|HOUR|MINUTE)S?");


    public static ParseResult parse(String toParse) {
        java.util.regex.Matcher matcher = PATTERN.matcher(toParse.toUpperCase());


        if (!matcher.matches()) {
            throw new IllegalArgumentException(toParse);
        }
        final int number = matcher.group(1) == null ? 1 : Integer.parseInt(matcher.group(1));
        final IntervalUnit unit = IntervalUnit.valueOf(matcher.group(2));
        return new ParseResult(number, unit);

    }


    protected final IntervalUnit unit;

    protected final int amount;

    protected ParsedInterval(ParseResult pair) {
        this(pair.amount(), pair.unit());
    }

    protected ParsedInterval(int amount, IntervalUnit unit) {
        this.amount = amount;
        this.unit = unit;
    }


    public abstract boolean isBucketBegin(T begin);

    public abstract boolean isBucketEnd(T end);

    @XmlValue
    public String getValue() {
        return (amount != 1 ? amount + " " : "") + unit + (amount != 1 ? "S" : "");
    }

    public abstract String print(T value);


    Duration getDuration() {
        return unit.getChronoField().getBaseUnit().getDuration().multipliedBy(amount);
    }

    @Override
    public String toString() {
        return getValue();
    }

    public record ParseResult(int amount, IntervalUnit unit) {
    }

}
