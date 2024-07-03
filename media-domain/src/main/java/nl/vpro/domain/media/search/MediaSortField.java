package nl.vpro.domain.media.search;

import java.util.Optional;
import java.util.Set;
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



    sortTitle(Type.STRING, null, "titles.main"),
    sortTitle_NPO(Type.STRING, null, "titles_NPO.main"),


    mid(Type.STRING),


    /**
     * @deprecated use {@link #mediaType} instead
     */
    @Deprecated
    type(Type.STRING, null),
    mediaType(Type.STRING) {
        @Override
        public String[] derivedFrom(Class<? extends MediaObject> clazz) {
            return new String[] {"type"};
        }
        @Override
        public Optional<String> nulls() {
            return Optional.of(MediaType.MEDIA.name());
        }
    },

    sortDate(INSTANT, "sortInstant", null) {
        @Override
        public String[] derivedFrom(Class<? extends MediaObject> clazz) {
            if (clazz.equals(Program.class)) {
                return new String[]{"scheduleEvents", "creationInstant"};
            } else if (clazz.equals(Segment.class)) {
                return new String[]{"parent"};
            } else {
                return super.derivedFrom(clazz);
            }
        }
        @Override
        public Set<Class<? extends MediaObject>> forClasses() {
            return Set.of(Program.class, Segment.class, Group.class);

        }
    },
    lastModified(INSTANT),
    creationDate(INSTANT, "creationInstant", "creationDate"),
    publishStop(INSTANT) {
        @Override
        public Optional<String> nulls() {
            return Optional.of(MAX_INSTANT);
        }

    },
    publishStart(INSTANT) {
        @Override
        public Optional<String> nulls() {
            return Optional.of(MIN_INSTANT);
        }
    },
    lastPublished(INSTANT),


    lastModifiedBy(Type.STRING, null, null),
    createdBy(Type.STRING, null, null),

    /**
     * Sort on location count
     */
    locations(Type.COUNT, "locations", "locationCount"),

    /**
     * @since 7.12
     */
    publishedLocations(Type.COUNT, "locations", "locationPublishedCount") {
        @Override
        public Predicate<Object> predicate() {
            return i -> ((Location) i).isConsiderableForPublication();
        }
    },

    memberofCount(Type.COUNT, "memberOf", null),
    episodeofCount(Type.COUNT, "episodeOf", null) {
        @Override
        public Set<Class<? extends MediaObject>> forClasses() {
            return Set.of(Program.class);
        }
    },
    scheduleEventsCount(Type.COUNT, "scheduleEvents", null) {
        @Override
        public Set<Class<? extends MediaObject>> forClasses() {
            return Set.of(Program.class);
        }
    },
    imagesCount(Type.COUNT, "images", "imagesCount"),

    /**
     * @since 7.12
     */
    publishedImagesCount(Type.COUNT, "images", "imagesPublishedCount") {
        @Override
        public Predicate<Object> predicate() {
            return i -> ((Image) i).isConsiderableForPublication();
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
         public Predicate<Object> predicate() {
             return i -> {
                Image img = (Image) i;
                return img.isConsiderableForPublication() && missingCredits(img);

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

    public String[] derivedFrom(Class<? extends MediaObject> clazz) {
        return new String[] {};
    }
    public Set<Class<? extends MediaObject>> forClasses() {
        return Set.of(MediaObject.class);
    }

    public Predicate<Object> predicate() {
        return null;
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
