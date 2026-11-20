package nl.vpro.domain.api.subtitles;

import jakarta.validation.*;

import java.util.Set;

import org.junit.jupiter.api.*;

import nl.vpro.domain.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks whether the {@link TextMatcherList}s of {@link SubtitlesSearch} are still validated (cascaded), even without explicit {@code @Valid} on the fields.
 */
class SubtitlesSearchTest {

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

    @Test
    void valid() {
        SubtitlesSearch search = SubtitlesSearch.builder()
            .mids(TextMatcherList.must(TextMatcher.must("WO_VPRO_123")))
            .types(TextMatcherList.must(TextMatcher.must("CAPTION")))
            .languages(TextMatcherList.must(TextMatcher.must("nl")))
            .build();
        assertThat(validator.validate(search)).isEmpty();
    }

    @Test
    void invalidMids() {
        SubtitlesSearch search = SubtitlesSearch.builder()
            .mids(TextMatcherList.must(TextMatcher.must("")))
            .build();
        assertInvalid(search, "mids");
    }

    @Test
    void invalidTypes() {
        SubtitlesSearch search = SubtitlesSearch.builder()
            .types(TextMatcherList.must(TextMatcher.must("")))
            .build();
        assertInvalid(search, "types");
    }

    @Test
    void invalidLanguages() {
        SubtitlesSearch search = SubtitlesSearch.builder()
            .languages(TextMatcherList.must(TextMatcher.must("")))
            .build();
        assertInvalid(search, "languages");
    }

    @Test
    void invalidRegex() {
        SubtitlesSearch search = SubtitlesSearch.builder()
            .types(TextMatcherList.must(TextMatcher.must("achter*[", StandardMatchType.REGEX)))
            .build();
        assertInvalid(search, "types");
    }

    private void assertInvalid(SubtitlesSearch search, String field) {
        Set<ConstraintViolation<SubtitlesSearch>> violations = validator.validate(search);
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .allSatisfy(v -> assertThat(v.getPropertyPath().toString()).startsWith(field));
    }
}
