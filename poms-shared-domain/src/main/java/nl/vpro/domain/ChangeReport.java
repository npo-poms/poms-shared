package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public class ChangeReport {

    boolean hasChanges = false;


    public void reset() {
        hasChanges = false;
    }

    public boolean hasChanges() {
        return hasChanges;
    }

    public void change(String field) {
        // we may want to
        change();
    }
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
}
