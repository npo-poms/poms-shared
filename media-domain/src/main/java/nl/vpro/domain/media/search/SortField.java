package nl.vpro.domain.media.search;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public interface SortField {

    String NULL_INSTANT = "1970-01-01T00:00:00Z";


    String name();

    Type type();

    default String nulls() {
        return type() == Type.INSTANT ? NULL_INSTANT : null;
    }

    default String property() {
        return name();
    }

    default String sortField() {
        return name();
    }

    enum Type {
        STRING,
        LONG,
        INSTANT
    }
}
