package nl.vpro.domain.media.search;

import nl.vpro.domain.AbstractPublishableObject_;
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
    creationDate(Type.LONG, AbstractPublishableObject_.creationInstant.getName()),
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
    episodeofCount(Type.LONG),
    scheduleEventsCount(Type.LONG),

    firstScheduleEvent(Type.LONG),
    firstScheduleEventNoRerun(Type.LONG),
    lastScheduleEvent(Type.LONG),
    lastScheduleEventNoRerun(Type.LONG)


    ;
    private final Type t;
    private final String field;

    MediaSortField(Type type) {
        this.t = type;
        this.field = name();
    }


    MediaSortField(Type type, String field) {
        this.t = type;
        this.field = field;
    }
    @Override
    public Type type() {
        return t;
    }

    @Override
    public String field() {
        return field;
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
