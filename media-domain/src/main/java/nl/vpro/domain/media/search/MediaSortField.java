package nl.vpro.domain.media.search;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum MediaSortField implements SortField {
    sortTitle(Type.STRING),

    mid(Type.STRING),
    type(Type.STRING),
    mediaType(Type.STRING),

    sortDate(Type.LONG),
    lastModified(Type.LONG),
    creationDate(Type.LONG),
    publishStop(Type.LONG),
    publishStart(Type.LONG),
    lastPublished(Type.LONG),


    lastModifiedBy(Type.STRING),
    createdBy(Type.STRING),

    locations(Type.LONG) {
        @Override
        public String field() {
            return "locationCount";
        }
    },
    memberofCount(Type.LONG),
    episodeofCount(Type.LONG)



    ;
    private final Type t;

    MediaSortField(Type type) {
        this.t = type;
    }
    @Override
    public Type type() {
        return t;
    }

    public static MediaSortField valueOfNullable(String string) {
        if (string == null) {
            return null;
        }
        return valueOf(string);
    }
}
