package nl.vpro.domain.media;

import lombok.Getter;

import nl.vpro.domain.media.update.GroupUpdate;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.domain.media.update.SegmentUpdate;

import static nl.vpro.domain.media.MediaType.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public interface EntityType {
    String name();

    MediaType getMediaType();

    enum AllMedia implements EntityType {
        media(MEDIA),
        program(PROGRAM),
        group(GROUP),
        segment(SEGMENT);

        @Getter
        private final MediaType mediaType;

        AllMedia(MediaType type) {
            this.mediaType = type;
        }

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

    enum Program implements EntityType {
        media(MEDIA),
        program(PROGRAM);
        @Getter
        private final MediaType mediaType;

        Program(MediaType type) {
            this.mediaType = type;

        }
    }

    enum Group implements EntityType {
        media(MEDIA),
        group(GROUP);
         @Getter
        private final MediaType mediaType;
        Group(MediaType type) {
            this.mediaType = type;

        }
    }

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
