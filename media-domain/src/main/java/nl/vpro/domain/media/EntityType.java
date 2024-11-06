package nl.vpro.domain.media;

import lombok.Getter;

import nl.vpro.domain.media.update.GroupUpdate;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.domain.media.update.SegmentUpdate;

import static nl.vpro.domain.media.MediaType.*;

/**
 * A number of enums that are mainly used to define possible set 'entity type'. Used e.g. in the media backend API
 * endpoint definitions.
 *
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public interface EntityType {
    String name();

    MediaType getMediaType();

    /**
     * Used as path-elements of endpoints that are relevant for any mediaobject.
     */
    enum AllMedia implements EntityType {
        /**
         * Any type of mediaobject.
         */
        media(MEDIA),
        /**
         * Just if it is a {@link nl.vpro.domain.media.Program}
         */
        program(PROGRAM),
        /**
         * Just if it is a {@link nl.vpro.domain.media.Group}
         */
        group(GROUP),
        /**
         * Just if it is a {@link nl.vpro.domain.media.Segment}
         */
        segment(SEGMENT);

        @Getter
        private final MediaType mediaType;

        AllMedia(MediaType type) {
            this.mediaType = type;
        }

        /**
         * Given some class, return the assiciated {@link AllMedia}. E.g. for {@link Program} or {@link ProgramUpdate}
         * it returns {@link #program}
         * @throws IllegalArgumentException if there is no sensible such mapping
         */
        public static AllMedia valueOf(Class<?> type) {
            if (nl.vpro.domain.media.Program.class.isAssignableFrom(type)) return program;
            if (ProgramUpdate.class.isAssignableFrom(type)) return program;
            if (nl.vpro.domain.media.Group.class.isAssignableFrom(type)) return group;
            if (GroupUpdate.class.isAssignableFrom(type)) return group;
            if (Segment.class.isAssignableFrom(type)) return segment;
            if (SegmentUpdate.class.isAssignableFrom(type)) return segment;
            if (MediaObject.class.isAssignableFrom(type)) return media;
            if (MediaUpdate.class.isAssignableFrom(type)) return media;
            throw new IllegalArgumentException();
        }

    }

    /**
     * Used as path-elements of endpoints that are relevant for {@link nl.vpro.domain.media.Program programs} only.
     * But {@link Program#media} is a sensible value then too.
     */
    enum Program implements EntityType {
        media(MEDIA),
        program(PROGRAM);
        @Getter
        private final MediaType mediaType;

        Program(MediaType type) {
            this.mediaType = type;

        }
    }

    /**
     * Used as path-elements of endpoints that are relevant for {@link nl.vpro.domain.media.Group groups} only.
     * But {@link Group#media} is a sensible value then too.
     */
    enum Group implements EntityType {
        media(MEDIA),
        group(GROUP);
         @Getter
        private final MediaType mediaType;
        Group(MediaType type) {
            this.mediaType = type;

        }
    }

    /**
     * Used as path-elements of endpoints that are relevant for {@link nl.vpro.domain.media.Program programs} and {@link Segment segments} only.
     */
    enum NoGroups implements EntityType {
        media(MEDIA),
        program(PROGRAM),
        segment(SEGMENT);
        @Getter
        private final MediaType mediaType;
        NoGroups(MediaType type) {
            this.mediaType = type;

        }
    }

    /**
     * Used as path-elements of endpoints that are relevant for {@link nl.vpro.domain.media.Program programs} and {@link nl.vpro.domain.media.Group groups} only.
     */
    enum NoSegments implements EntityType {
        media(MEDIA),
        program(PROGRAM),
        group(GROUP);
        @Getter
        private final MediaType mediaType;
        NoSegments(MediaType type) {
            this.mediaType = type;

        }
    }
}
