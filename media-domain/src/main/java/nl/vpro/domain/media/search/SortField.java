package nl.vpro.domain.media.search;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public interface SortField {

    String NULL_INSTANT = "1970-01-01T00:00:00Z";
    String MIN_INSTANT = "1900-01-01T00:00:00Z";
    String MAX_INSTANT = "2500-01-01T00:00:00Z";


    String name();

    Type type();

    default String nulls() {
        return switch(type()) {
            case INSTANT -> NULL_INSTANT;
            case COUNT -> "0";
            case STRING -> "";
        };
    }

    /**
     * The associated property or <code>null</code> if there is none (this field will require manual mapping)
     *
     */
    default String property() {
        return null;
    }

    default String sortField() {
        return name();
    }

    enum Type {
        STRING,
        COUNT,
        INSTANT
    }
}
