/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

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
        for (MediaType type : MediaType.values()) {
            log.info("\n\n" + type.name() + " " + type.toString());
            log.info("{}", Arrays.asList(type.allowedEpisodeOfTypes()));
            log.info("{}",Arrays.asList(type.allowedEpisodeTypes()));
            log.info("{}",Arrays.asList(type.allowedMemberOfTypes()));
            log.info("{}",Arrays.asList(type.allowedMemberTypes()));
            log.info("{}",type.getMediaClass());
            try {
                log.info("{}",type.getMediaInstance());
            } catch (RuntimeException rte) {
                assertThat(type).isEqualTo(MediaType.MEDIA);
            }
            log.info("{}",type.getMediaObjectClass());
            log.info("{}",type.getSubType());
            log.info("{}",type.getSubTypes());
            log.info("{}",type.hasEpisodeOf());
            log.info("{}",type.canContainEpisodes());
            log.info("{}",type.hasMemberOf());
            log.info("{}",type.hasMembers());
            log.info("{}",type.hasOrdering());
            log.info("{}",type.hasSegments());
            log.info("{}",Arrays.asList(type.preferredEpisodeOfTypes()));
            log.info("{}",Arrays.asList(type.preferredEpisodeTypes()));
            log.info("{}",Arrays.asList(type.preferredMemberOfTypes()));
            log.info("{}",Arrays.asList(type.preferredMemberTypes()));
            for (MediaType t : MediaType.values()) {
                log.info("{} ct {}: {}", type, t, type.compareTo(t));
            }
            try {
                MediaObject instance = type.getMediaInstance();
                log.info("{} -> {}", type, instance);
            } catch (RuntimeException e) {
                assertThat(e.getMessage()).startsWith("Not possible");
                log.info("Ok, not posible to instantiate {}", type);
            }
            if (type.getSubType() != null) {
                assertThat(type.getSubType().getMediaType()).withFailMessage("" + type).isNotNull();
            }
        }
    }
}
