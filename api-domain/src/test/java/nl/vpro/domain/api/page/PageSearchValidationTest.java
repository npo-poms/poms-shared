package nl.vpro.domain.api.page;

import jakarta.validation.*;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.domain.api.*;
import nl.vpro.domain.page.LinkType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks whether the matcher lists of {@link PageSearch} are validated (cascaded).
 */
class PageSearchValidationTest {

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

    static RelationSearch invalidRelationSearch() {
        RelationSearch relationSearch = new RelationSearch();
        relationSearch.setTypes(invalidText());
        return relationSearch;
    }

    static AssociationSearch invalidAssociationSearch() {
        AssociationSearch associationSearch = new AssociationSearch();
        associationSearch.setUrls(invalidText());
        return associationSearch;
    }

    static Stream<Arguments> invalidSearches() {
        return Stream.of(
            Arguments.of("broadcasters", (Consumer<PageSearch>) s -> s.setBroadcasters(invalidText())),
            Arguments.of("types", (Consumer<PageSearch>) s -> s.setTypes(invalidText())),
            Arguments.of("portals", (Consumer<PageSearch>) s -> s.setPortals(invalidText())),
            Arguments.of("sections", (Consumer<PageSearch>) s -> s.setSections(invalidText())),
            Arguments.of("genres", (Consumer<PageSearch>) s -> s.setGenres(invalidText())),
            Arguments.of("tags", (Consumer<PageSearch>) s -> s.setTags(invalidExtendedText())),
            Arguments.of("keywords", (Consumer<PageSearch>) s -> s.setKeywords(invalidExtendedText())),
            Arguments.of("relations", (Consumer<PageSearch>) s -> s.setRelations(new RelationSearchList(invalidRelationSearch()))),
            Arguments.of("links", (Consumer<PageSearch>) s -> s.setLinks(new AssociationSearchList(invalidAssociationSearch()))),
            Arguments.of("referrals", (Consumer<PageSearch>) s -> s.setReferrals(new AssociationSearchList(invalidAssociationSearch())))
        );
    }

    @Test
    void valid() {
        PageSearch search = PageSearch.builder()
            .types(TextMatcherList.must(TextMatcher.must("ARTICLE")))
            .tags(ExtendedTextMatcherList.must(ExtendedTextMatcher.must("foo")))
            .links(new AssociationSearchList(AssociationSearch.of(LinkType.TOP_STORY)))
            .build();
        assertThat(validator.validate(search)).isEmpty();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSearches")
    void invalid(String field, Consumer<PageSearch> setter) {
        PageSearch search = new PageSearch();
        setter.accept(search);
        Set<ConstraintViolation<PageSearch>> violations = validator.validate(search);
        assertThat(violations).isNotEmpty();
        assertThat(violations)
            .allSatisfy(v -> assertThat(v.getPropertyPath().toString()).startsWith(field));
    }
}
