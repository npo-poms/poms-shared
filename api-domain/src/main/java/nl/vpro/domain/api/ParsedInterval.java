package nl.vpro.domain.api;

import lombok.Getter;

import java.time.Duration;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Getter
abstract class ParsedInterval<T extends Comparable<T>> {


    private final Unit unit;

    private final int amount;

    protected ParsedInterval(int amount, Unit unit) {
        this.amount = amount;
        this.unit = unit;
    }


    public abstract boolean isBucketBegin(T begin);

    public abstract boolean isBucketEnd(T end);

    public String print(T value) {
        return "";
    }


    Duration getDuration() {
        return unit.getChronoField().getBaseUnit().getDuration();
    }



}
