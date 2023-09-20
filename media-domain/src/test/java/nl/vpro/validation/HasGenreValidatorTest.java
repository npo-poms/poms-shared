package nl.vpro.validation;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.MediaIdentifiable;
import nl.vpro.domain.media.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 7.8
 */
public class HasGenreValidatorTest {

    @Getter
    public static class A implements MediaIdentifiable {
        private final String mid;
        private final MediaType mediaType;
        private final List<String> genres;
        public A(MediaType mediaType, String... genres) {
            this.mid = null;
            this.mediaType = mediaType;
            this.genres = Arrays.asList(genres);
        }


    }


    @Test
    public void testIsValid() {
        HasGenreValidator validator = new HasGenreValidator();
        assertThat(validator.isValid(
            new A(MediaType.COLLECTION), null)).isTrue();

        assertThat(validator.isValid(
            new A(MediaType.BROADCAST), null)).isFalse();

    }


}
