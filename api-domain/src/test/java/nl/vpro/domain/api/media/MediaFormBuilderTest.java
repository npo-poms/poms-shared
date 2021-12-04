package nl.vpro.domain.api.media;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class MediaFormBuilderTest {

    @Test
    void buildNull() {
        assertThat(MediaFormBuilder.from(null).build()).isEqualTo(new MediaForm());
    }

    @Test
    void text() {
        assertThat(MediaFormBuilder.form().text("foobar").build().getText()).isEqualTo("foobar");
        assertThat(MediaFormBuilder.form().text("foobar").build().getSearches()).isEqualTo(
            MediaSearch.builder().text(SimpleTextMatcher.builder().value("foobar").match(Match.SHOULD).matchType(SimpleMatchType.TEXT).build()).build());
    }

    @Test
    void more() {
        MediaForm form = MediaFormBuilder.form()
            .publishDate(Instant.parse("2021-12-05T15:44:00Z"), Instant.parse("2210-01-01T00:00:00Z"))
            .broadcasters("VPRO", "EO")
            .asc(MediaSortField.member)
            .text("foobar").build();

        assertThat(form.getSearches().getPublishDates().toString()).isEqualTo("MUST:[RangeMatcher{begin=2021-12-05T15:44:00Z, end=2210-01-01T00:00:00Z, inclusiveEnd=false}]");
        assertThat(form.getSearches().getBroadcasters().toString()).isEqualTo("MUST:[TextMatcher{value='VPRO', match=SHOULD, matchType=TEXT}, TextMatcher{value='EO', match=SHOULD, matchType=TEXT}]");

    }

}
