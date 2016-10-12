/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.Arrays;

import org.junit.Test;

import static nl.vpro.domain.media.MediaType.*;
import static org.assertj.core.api.Assertions.assertThat;

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
            System.out.println("\n\n" + type.name() + " " + type.toString());
            System.out.println(Arrays.asList(type.allowedEpisodeOfTypes()));
            System.out.println(Arrays.asList(type.allowedEpisodeTypes()));
            System.out.println(Arrays.asList(type.allowedMemberOfTypes()));
            System.out.println(Arrays.asList(type.allowedMemberTypes()));
            System.out.println(type.getMediaClass());
            try {
                System.out.println(type.getMediaInstance());
            } catch (RuntimeException rte) {
                assertThat(type).isEqualTo(MediaType.MEDIA);
            }
            System.out.println(type.getMediaObjectClass());
            System.out.println(type.getSubType());
            System.out.println(type.hasEpisodeOf());
            System.out.println(type.hasEpisodes());
            System.out.println(type.hasMemberOf());
            System.out.println(type.hasMembers());
            System.out.println(type.hasOrdering());
            System.out.println(type.hasSegments());
            System.out.println(Arrays.asList(type.preferredEpisodeOfTypes()));
            System.out.println(Arrays.asList(type.preferredEpisodeTypes()));
            System.out.println(Arrays.asList(type.preferredMemberOfTypes()));
            System.out.println(Arrays.asList(type.preferredMemberTypes()));
            for (MediaType t : MediaType.values()) {
                System.out.print(type.compareTo(t));
            }
        }
    }
}
