package nl.vpro.domain.api.media;

import jakarta.validation.*;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.domain.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks whether the matcher lists of {@link MediaSearch} are validated (cascaded).
 */
class MediaSearchValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void shutdown() {
        factory.close();
    }

    static TextMatcherList invalidText() {
        return TextMatcherList.must(TextMatcher.must(""));
    }

    static ExtendedTextMatcherList invalidExtendedText() {
        return ExtendedTextMatcherList.must(ExtendedTextMatcher.must(""));
    }

    static Stream<Arguments> invalidSearches() {
        return Stream.of(
            Arguments.of("mediaIds", (Consumer<MediaSearch>) s -> s.setMediaIds(invalidText())),
            Arguments.of("types", (Consumer<MediaSearch>) s -> s.setTypes(invalidText())),
            Arguments.of("avTypes", (Consumer<MediaSearch>) s -> s.setAvTypes(invalidText())),
            Arguments.of("broadcasters", (Consumer<MediaSearch>) s -> s.setBroadcasters(invalidText())),
            Arguments.of("locations", (Consumer<MediaSearch>) s -> s.setLocations(invalidText())),
            Arguments.of("tags", (Consumer<MediaSearch>) s -> s.setTags(invalidExtendedText())),
            Arguments.of("genres", (Consumer<MediaSearch>) s -> s.setGenres(invalidText())),
            Arguments.of("descendantOf", (Consumer<MediaSearch>) s -> s.setDescendantOf(invalidText())),
            Arguments.of("episodeOf", (Consumer<MediaSearch>) s -> s.setEpisodeOf(invalidText())),
            Arguments.of("memberOf", (Consumer<MediaSearch>) s -> s.setMemberOf(invalidText())),
            Arguments.of("ageRatings", (Consumer<MediaSearch>) s -> s.setAgeRatings(invalidText())),
            Arguments.of("contentRatings", (Consumer<MediaSearch>) s -> s.setContentRatings(invalidText())),
            Arguments.of("relations", (Consumer<MediaSearch>) s -> s.setRelations(
                new RelationSearchList(RelationSearch.builder().types(invalidText()).build())))
        );
    }

    @Test
    void valid() {
        MediaSearch search = new MediaSearch();
        search.setTypes(TextMatcherList.must(TextMatcher.must("CLIP")));
        search.setTags(ExtendedTextMatcherList.must(ExtendedTextMatcher.must("foo")));
        search.setRelations(new RelationSearchList(RelationSearch.builder().types(TextMatcherList.must(TextMatcher.must("LABEL"))).build()));
        assertThat(validator.validate(search)).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSearches")
    void invalid(String field, Consumer<MediaSearch> setter) {
        MediaSearch search = new MediaSearch();
        setter.accept(search);
        Set<ConstraintViolation<MediaSearch>> violations = validator.validate(search);
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .allSatisfy(v -> assertThat(v.getPropertyPath().toString()).startsWith(field));
    }

    @Test
    void invalidRegex() {
        MediaSearch search = new MediaSearch();
        search.setTypes(TextMatcherList.must(TextMatcher.must("achter*[", StandardMatchType.REGEX)));
        assertThat(validator.validate(search)).isNotEmpty();
    }
}
