package nl.vpro.rs.media;

import lombok.Getter;

import nl.vpro.domain.media.MediaType;

import static nl.vpro.domain.media.MediaType.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public
interface EntityType {
    String name();

    MediaType getMediaType();

    enum AllMedia implements EntityType {
        media(MEDIA),
        program(PROGRAM),
        group(GROUP),
        segment(SEGMENT);

        @Getter
        private MediaType mediaType;

        AllMedia(MediaType type) {
            this.mediaType = type;

        }
    }

    enum Program implements EntityType {
        media(MEDIA),
        program(PROGRAM);
        @Getter
        private MediaType mediaType;

        Program(MediaType type) {
            this.mediaType = type;

        }
    }

    enum Group implements EntityType {
        media(MEDIA),
        group(GROUP);
         @Getter
        private MediaType mediaType;
        Group(MediaType type) {
            this.mediaType = type;

        }
    }

    enum NoGroups implements EntityType {
        media(MEDIA),
        program(PROGRAM),
        segment(SEGMENT);
        @Getter
        private MediaType mediaType;
        NoGroups(MediaType type) {
            this.mediaType = type;

        }
    }

    enum NoSegments implements EntityType {
        media(MEDIA),
        program(PROGRAM),
        group(GROUP);
        @Getter
        private MediaType mediaType;
        NoSegments(MediaType type) {
            this.mediaType = type;

        }
    }
}
