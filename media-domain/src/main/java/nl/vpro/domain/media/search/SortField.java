package nl.vpro.domain.media.search;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public interface SortField {


    String name();

    Type type();

    default String field() {
        return name();
    }

     default String sortField() {
        return name();
    }

    enum Type {
        STRING,
        LONG
    }
}
