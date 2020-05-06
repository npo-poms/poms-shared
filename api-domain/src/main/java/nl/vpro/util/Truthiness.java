package nl.vpro.util;

import java.util.function.BooleanSupplier;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public enum Truthiness implements BooleanSupplier {
    TRUE(true),
    PROBABLY(true),
    UNKNOWN(true),
    FALSE(false);

    public static Truthiness of(boolean boolValue) {
        return boolValue ? Truthiness.TRUE : Truthiness.FALSE;
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
