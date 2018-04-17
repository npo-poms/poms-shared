package nl.vpro.domain.media.search;

import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum MediaSortField implements SortField {
    sortTitle(Type.STRING),
    sortTitle_NPO(Type.STRING),


    mid(Type.STRING),
    type(Type.STRING),
    mediaType(Type.STRING),

    sortDate(Type.LONG),
    lastModified(Type.LONG),
    creationDate(Type.LONG, "creationInstant", "creationDate"),
    publishStop(Type.LONG),
    publishStart(Type.LONG),
    lastPublished(Type.LONG),


    lastModifiedBy(Type.STRING),
    createdBy(Type.STRING),

    locations(Type.LONG, "locations", "locationCount"),

    memberofCount(Type.LONG),
    episodeofCount(Type.LONG),
    scheduleEventsCount(Type.LONG),

    firstScheduleEvent(Type.LONG),
    firstScheduleEventNoRerun(Type.LONG),
    lastScheduleEvent(Type.LONG),
    lastScheduleEventNoRerun(Type.LONG)


    ;
    private final Type t;
    private final String field;
    private final String sortField;

    MediaSortField(Type type) {
        t = type;
        field = name();
        sortField = name();
    }


    MediaSortField(Type type, String field, String sortField) {
        this.t = type;
        this.field = field == null ? name() : field;
        this.sortField = sortField == null ? name() : sortField;
    }
    @Override
    public Type type() {
        return t;
    }

    @Override
    public String field() {
        return field;
    }


    @Override
    public String sortField() {
        return sortField;
    }


    public static MediaSortField valueOfNullable(String string) {
        if (string == null) {
            return null;
        }
        return valueOf(string);
    }


    public static MediaSortField valueOf(MediaSortField sortTitle, OwnerType ownerType) {
        return valueOf(sortTitle.name() + "_" + ownerType.name());
    }
}
