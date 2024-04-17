package nl.vpro.domain.media.search;

import java.util.function.Predicate;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;

import static nl.vpro.domain.media.search.SortField.Type.INSTANT;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public enum MediaSortField implements SortField {



    sortTitle(Type.STRING, null),
    sortTitle_NPO(Type.STRING, null),


    mid(Type.STRING),

    @Deprecated
    type(Type.STRING, null),
    mediaType(Type.STRING) {
        @Override
        public String[] derivedFrom() {
            return new String[] {"type"};
        }
        @Override
        public String nulls() {
            return MediaType.MEDIA.name();
        }
    },

    sortDate(INSTANT, "sortInstant", null) {
        @Override
        public String[] derivedFrom() {
            return new String[] {"creationInstant"};
        }
    },
    lastModified(INSTANT),
    creationDate(INSTANT, "creationInstant", "creationDate"),
    publishStop(INSTANT),
    publishStart(INSTANT),
    lastPublished(INSTANT),


    lastModifiedBy(Type.STRING),
    createdBy(Type.STRING),

    /**
     * Sort on location count
     */
    locations(Type.COUNT, "locations", "locationCount"),

    /**
     * @since 7.12
     */
    publishedLocations(Type.COUNT, "locations", "locationPublishedCount") {
        @Override
        public Predicate<?> predicate() {
            return i -> ((Location) i).isPublishable();
        }
    },

    memberofCount(Type.COUNT, "memberOf", null),
    episodeofCount(Type.COUNT, "episodeOf", null) {
        @Override
        public Class<? extends MediaObject> forClass() {
            return Program.class;
        }
    },
    scheduleEventsCount(Type.COUNT, "scheduleEvents", null) {
        @Override
        public Class<? extends MediaObject> forClass() {
            return Program.class;
        }
    },
    imagesCount(Type.COUNT, "images", "imagesCount"),

    /**
     * @since 7.12
     */
    publishedImagesCount(Type.COUNT, "images", "imagesPublishedCount") {
        @Override
        public Predicate<?> predicate() {
            return i -> ((Image) i).isPublishable();
        }
    },
     /**
     * @since 7.12
     */
     imagesWithoutCreditsCount(Type.COUNT, "images", "imagesWithoutCreditsCount") {
         private boolean missingCredits(Image img) {
             return img.getCredits() == null || img.getCredits().trim().isEmpty() || img.getLicense() == null || img.getSource() == null || img.getSource().trim().isEmpty();
         }
         @Override
         public Predicate<?> predicate() {
             return i -> {
                Image img = (Image) i;
                return img.isPublishable() && missingCredits(img);

             };
         }
     },

    firstScheduleEvent(INSTANT, null ),
    firstScheduleEventNoRerun(INSTANT, null),
    lastScheduleEvent(INSTANT, null),
    lastScheduleEventNoRerun(INSTANT, null)
    ;




    private final Type t;
    private final String property;
    private final String sortField;


    MediaSortField(Type type) {
        t = type;
        property = name();
        sortField = name();
    }
    MediaSortField(Type type, String property) {
        this(type, property, null);

    }


    MediaSortField(Type type, String property, String sortField) {
        this.t = type;
        this.property = property;
        this.sortField = sortField == null ? name() : sortField;
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

    public String[] derivedFrom() {
        return new String[] {};
    }

    public Predicate<?> predicate() {
        return null;
    }

    public Class<? extends MediaObject> forClass() {
        return MediaObject.class;
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
