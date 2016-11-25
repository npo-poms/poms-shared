package nl.vpro.domain.media.search;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum MediaSortField implements SortField {
    lastModified(Type.LONG),
    sortTitle(Type.STRING),
    creationDate(Type.LONG),
    locations(Type.LONG) {
        @Override
        public String field() {
            return "locationCount";
        }
    },
    sortDate(Type.LONG);

    private final Type type;

    MediaSortField(Type type) {
        this.type = type;
    }
    @Override
    public Type type() {
        return type;
    }
}
