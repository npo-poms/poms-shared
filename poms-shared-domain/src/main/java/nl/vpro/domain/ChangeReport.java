package nl.vpro.domain;

import java.util.function.BooleanSupplier;

/**
 * A small wrapper for a {@code boolean}, which can be used to keep track of whether
 * some process results in changes, and things may need saving.
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public class ChangeReport implements BooleanSupplier  {

    boolean hasChanges = false;


    public void reset() {
        hasChanges = false;
    }

    public boolean hasChanges() {
        return hasChanges;
    }

    /**
     * Marks the report has having changes, because of a change in given field.
     * <p>
     * Currently just calls {@link #change()},
     */
    public void change(String field) {

        change();
    }

    /**
     * Marks the report has having changes.
    */
    public void change() {
        hasChanges = true;
    }

    public ChangeReport or(ChangeReport other) {
        ChangeReport result = new ChangeReport();
        result.hasChanges = hasChanges || other.hasChanges;
        return result;
    }

    public void also(ChangeReport other ) {
        this.hasChanges = this.hasChanges | other.hasChanges;
    }

    @Override
    public boolean getAsBoolean() {
        return hasChanges;
    }
}
