package nl.vpro.domain.media.search;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum LocationSortField implements SortField {
    lastModified(Type.LONG),
    creationDate(Type.LONG)
    ;

    private final Type type;

    LocationSortField(Type type) {
        this.type = type;
    }

    @Override
    public Type type() {
        return type;
    }

}
