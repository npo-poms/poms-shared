/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;

import nl.vpro.logging.simple.*;

import static java.util.Arrays.asList;
import static nl.vpro.domain.media.MediaType.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MediaTypeTest {

    @Test
    public void testPreferredEpisodeTypes() {
        assertThat(SERIES.preferredEpisodeTypes()).containsOnly(BROADCAST);
        assertThat(SEASON.preferredEpisodeTypes()).containsOnly(BROADCAST);

        assertThat(MOVIE.preferredEpisodeTypes()).isEmpty();
        assertThat(TRAILER.preferredEpisodeTypes()).isEmpty();
        assertThat(ALBUM.preferredEpisodeTypes()).isEmpty();
        assertThat(COLLECTION.preferredEpisodeTypes()).isEmpty();
    }

    @Test
    public void testAllowedEpisodeTypes() {
        assertThat(SERIES.allowedEpisodeTypes()).containsOnly(BROADCAST);
        assertThat(SEASON.allowedEpisodeTypes()).containsOnly(BROADCAST);

        assertThat(MOVIE.allowedEpisodeTypes()).isEmpty();
        assertThat(TRAILER.allowedEpisodeTypes()).isEmpty();
        assertThat(ALBUM.allowedEpisodeTypes()).isEmpty();
        assertThat(COLLECTION.allowedEpisodeTypes()).isEmpty();
    }

    @Test
    public void testPreferredEpisodeOfTypes() {
        assertThat(BROADCAST.preferredEpisodeOfTypes()).containsOnly(SERIES, SEASON);
        assertThat(STRAND.preferredEpisodeOfTypes()).containsOnly(SERIES, SEASON);

        assertThat(CLIP.allowedEpisodeTypes()).isEmpty();
        assertThat(PLAYLIST.allowedEpisodeTypes()).isEmpty();
        assertThat(STRAND.allowedEpisodeTypes()).isEmpty();
    }

    @Test
    public void testAllowedEpisodeOfTypesOnTypeWithoutEpisodeOf() {
        assertThat(SEASON.allowedEpisodeOfTypes()).isEmpty();
    }

    @Test
    public void testAllowedEpisodeOfTypes() {
        assertThat(STRAND.allowedEpisodeOfTypes()).containsOnly(SERIES, SEASON);

        assertThat(SEGMENT.allowedEpisodeOfTypes()).isEmpty();
        assertThat(SERIES.allowedEpisodeOfTypes()).isEmpty();
    }

    @Test
    public void testAllowedMemberType() {
        assertThat(PLAYLIST.allowedMemberTypes()).containsOnly(MEDIA);
    }

    @Test
    public void testAllowedMemberOfType() {
        assertThat(SEGMENT.allowedMemberOfTypes()).containsOnly(MEDIA);
    }

    @Test
    public void testAllowedEpisodeTypeOnArchive() {
        assertThat(COLLECTION.allowedEpisodeTypes()).isEmpty();
        //assertThat(ARCHIVE.allowedEpisodeTypes()).isEmpty(); deprecated
    }

    @Test
    public void noErrors() {
        SimpleLogger slog = StringBuilderSimpleLogger.builder().level(Level.DEBUG).build().chain(new Slf4jSimpleLogger(log).withThreshold(Level.INFO));
        for (MediaType type : MediaType.values()) {
            slog.debug("\n\n" + type.name() + " " + type);
            slog.debug("prefix {}", type.getUrnPrefix());
            slog.debug("{}", asList(type.allowedEpisodeOfTypes()));
            slog.debug("{}", asList(type.allowedEpisodeTypes()));
            slog.debug("{}", asList(type.allowedMemberOfTypes()));
            slog.debug("{}", asList(type.allowedMemberTypes()));
            slog.debug("{}",type.getMediaClass());
            try {
                slog.debug("{}",type.getMediaInstance());
            } catch (RuntimeException rte) {
                assertThat(type).isEqualTo(MediaType.MEDIA);
            }
            slog.debug("{}",type.getMediaObjectClass());
            slog.debug("{}",type.getSubType());
            slog.debug("{}",type.getSubTypes());
            slog.debug("{}",type.hasEpisodeOf());
            slog.debug("{}",type.canContainEpisodes());
            slog.debug("{}",type.hasMemberOf());
            slog.debug("{}",type.hasMembers());
            slog.debug("{}",type.hasOrdering());
            slog.debug("{}",type.hasSegments());
            slog.debug("{}", asList(type.preferredEpisodeOfTypes()));
            slog.debug("{}", asList(type.preferredEpisodeTypes()));
            slog.debug("{}", asList(type.preferredMemberOfTypes()));
            slog.debug("{}", asList(type.preferredMemberTypes()));
            for (MediaType t : MediaType.values()) {
                slog.debug("{} ct {}: {}", type, t, type.compareTo(t));
            }
            try {
                MediaObject instance = type.getMediaInstance();
                slog.debug("{} -> {}", type, instance);
            } catch (RuntimeException e) {
                assertThat(e.getMessage()).startsWith("Not possible");
                slog.debug("Ok, not possible to instantiate {}", type);
            }
            if (type.getSubType() != null) {
                assertThat(type.getSubType().getMediaType()).withFailMessage(String.valueOf(type)).isNotNull();
            }
            if (type != MEDIA) {
                assertThat(type.getUrnPrefix()).isNotNull();
            }
        }
    }
}
