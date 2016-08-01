/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

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
}
