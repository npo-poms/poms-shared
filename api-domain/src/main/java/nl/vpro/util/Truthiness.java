package nl.vpro.util;

import java.util.function.BooleanSupplier;

/**
 * @author Michiel Meeuwissen
 * @since ...
 * TODO: move to vpro-shared.
 */
public enum Truthiness implements BooleanSupplier {
    /**
     * Definitely true
     */
    TRUE(true),
    /**
     * We are not sure, but we guess it's true
     */
    PROBABLY(true),
    /**
     * We really don't know
     */
    UNKNOWN(true),
    /**
     * It might be true, but provisionally we think it's not.
     * E.g. the condition may become true for some later reason.
     */
    MAYBE_NOT(false),
    FALSE(false);

    public static Truthiness of(boolean boolValue) {
        return boolValue ? Truthiness.TRUE : Truthiness.FALSE;
    }

    public static Truthiness maybe(boolean boolValue) {
        return boolValue ? Truthiness.TRUE : Truthiness.MAYBE_NOT;
    }


    private final boolean boolValue;

    Truthiness(boolean boolValue) {
        this.boolValue = boolValue;
    }

    @Override
    public boolean getAsBoolean() {
        return boolValue;

    }
}
