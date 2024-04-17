package nl.vpro.domain.media.search;

import lombok.Getter;

import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.support.OwnerType;

import static nl.vpro.domain.media.search.MediaSortField.MapType.*;
import static nl.vpro.domain.media.search.SortField.Type.INSTANT;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum MediaSortField implements SortField {



    sortTitle(Type.STRING, NONE),
    sortTitle_NPO(Type.STRING, NONE),


    mid(Type.STRING),

    @Deprecated
    type(Type.STRING),
    mediaType(Type.STRING, null, null, MediaType.MEDIA.name(), "type", SIMPLE),

    sortDate(INSTANT, "sortInstant", "sortDate", null, null, SIMPLE),
    lastModified(INSTANT),
    creationDate(INSTANT, "creationInstant", "creationDate", null, null, SIMPLE),
    publishStop(INSTANT),
    publishStart(INSTANT),
    lastPublished(INSTANT),


    lastModifiedBy(Type.STRING),
    createdBy(Type.STRING),

    /**
     * Sort on location count
     */
    locations(Type.LONG, "locations", "locationCount", null, null, COUNT),

    memberofCount(Type.LONG, "memberOf", null, "0", null, COUNT),
    episodeofCount(Type.LONG, "episodeOf", null, "0", null, NONE),
    scheduleEventsCount(Type.LONG, "scheduleEvents", null, "0", null, NONE),

    firstScheduleEvent(INSTANT, NONE),
    firstScheduleEventNoRerun(INSTANT, NONE),
    lastScheduleEvent(INSTANT, NONE),
    lastScheduleEventNoRerun(INSTANT, NONE)
    ;

    public enum MapType {
        SIMPLE,
        NONE,
        COUNT
    }


    private final Type t;
    private final String property;
    private final String sortField;
    private final String nulls;
    private final String derivedFrom;
    @Getter
    private final MapType mapType;

    MediaSortField(Type type, MapType mapType) {
        t = type;
        property = name();
        sortField = name();
        nulls = type == INSTANT ? NULL_INSTANT : null;
        derivedFrom = null;
        this.mapType = mapType;
    }
     MediaSortField(Type type) {
        this(type, SIMPLE);
    }


    MediaSortField(Type type, String property, String sortField, String nulls, String derivedFrom, MapType mapType) {
        this.t = type;
        this.property = property == null ? name() : property;
        this.sortField = sortField == null ? name() : sortField;
        this.nulls = nulls == null ? (type == INSTANT ? NULL_INSTANT : null) : nulls;
        this.derivedFrom = derivedFrom;
        this.mapType = mapType;
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
