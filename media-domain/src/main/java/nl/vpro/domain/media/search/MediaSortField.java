package nl.vpro.domain.media.search;

import lombok.Getter;

import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.support.OwnerType;

import static nl.vpro.domain.media.search.SortField.Type.INSTANT;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum MediaSortField implements SortField {



    sortTitle(Type.STRING, false),
    sortTitle_NPO(Type.STRING, false),


    mid(Type.STRING),
    type(Type.STRING),
    mediaType(Type.STRING, null, null, MediaType.MEDIA.name(), "type"),

    sortDate(INSTANT, "sortInstant", "sortDate", null, null),
    lastModified(INSTANT),
    creationDate(INSTANT, "creationInstant", "creationDate", null, null),
    publishStop(INSTANT),
    publishStart(INSTANT),
    lastPublished(INSTANT),


    lastModifiedBy(Type.STRING),
    createdBy(Type.STRING),

    locations(Type.LONG, "locations", "locationCount", null, null),

    memberofCount(Type.LONG, false),
    episodeofCount(Type.LONG, false),
    scheduleEventsCount(Type.LONG, false),

    firstScheduleEvent(INSTANT, false),
    firstScheduleEventNoRerun(INSTANT, false),
    lastScheduleEvent(INSTANT, false),
    lastScheduleEventNoRerun(INSTANT, false)


    ;


    private final Type t;
    private final String property;
    private final String sortField;
    private final String nulls;
    private final String derivedFrom;
    @Getter
    private final boolean autoMappable ;

    MediaSortField(Type type, boolean autoMappable) {
        t = type;
        property = name();
        sortField = name();
        nulls = type == INSTANT ? NULL_INSTANT : null;
        derivedFrom = null;
        this.autoMappable = autoMappable;
    }
     MediaSortField(Type type) {
        this(type, true);
    }


    MediaSortField(Type type, String property, String sortField, String nulls, String derivedFrom) {
        this.t = type;
        this.property = property == null ? name() : property;
        this.sortField = sortField == null ? name() : sortField;
        this.nulls = nulls == null ? (type == INSTANT ? NULL_INSTANT : null) : nulls;
        this.derivedFrom = derivedFrom;
        autoMappable = true;
    }
    @Override
    public Type type() {
        return t;
    }

    @Override
    public String property() {
        return property;
    }

    @Override
    public String sortField() {
        return sortField;
    }

    @Override
    public String nulls() {
        return nulls;
    }



    public String derivedFrom() {
        return derivedFrom;
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
