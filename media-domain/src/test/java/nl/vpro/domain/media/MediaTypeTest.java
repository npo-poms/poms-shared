/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.StringBuilderSimpleLogger;

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
        SimpleLogger slog = StringBuilderSimpleLogger.builder().level(Level.DEBUG).build().chain(SimpleLogger.slfj4(log).withThreshold(Level.INFO));
        for (MediaType type : MediaType.values()) {
            slog.debug("\n\n" + type.name() + " " + type);
            slog.debug("{}", Arrays.asList(type.allowedEpisodeOfTypes()));
            slog.debug("{}",Arrays.asList(type.allowedEpisodeTypes()));
            slog.debug("{}",Arrays.asList(type.allowedMemberOfTypes()));
            slog.debug("{}",Arrays.asList(type.allowedMemberTypes()));
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
            slog.debug("{}",Arrays.asList(type.preferredEpisodeOfTypes()));
            slog.debug("{}",Arrays.asList(type.preferredEpisodeTypes()));
            slog.debug("{}",Arrays.asList(type.preferredMemberOfTypes()));
            slog.debug("{}",Arrays.asList(type.preferredMemberTypes()));
            for (MediaType t : MediaType.values()) {
                slog.debug("{} ct {}: {}", type, t, type.compareTo(t));
            }
            try {
                MediaObject instance = type.getMediaInstance();
                slog.debug("{} -> {}", type, instance);
            } catch (RuntimeException e) {
                assertThat(e.getMessage()).startsWith("Not possible");
                slog.debug("Ok, not posible to instantiate {}", type);
            }
            if (type.getSubType() != null) {
                assertThat(type.getSubType().getMediaType()).withFailMessage("" + type).isNotNull();
            }
        }
    }
}
