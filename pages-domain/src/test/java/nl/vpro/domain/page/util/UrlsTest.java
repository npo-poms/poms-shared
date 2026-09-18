/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.util;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.page.Portal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
class UrlsTest {

    @Test
    void normalizeEndingSlash() {
        String input = "http://www.vpro.nl/";
        String expected = "http://www.vpro.nl";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    void normalizeEndingSlashWithQuery() {
        String input = "http://www.vpro.nl/folder/?a";
        String expected = "http://www.vpro.nl/folder?a";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    void normalizeEndingSlashWithHashTag() {
        String input = "http://www.vpro.nl/folder/#/hashtag";
        String expected = "http://www.vpro.nl/folder#/hashtag";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    void normalizeEncode() {
        String input = "http://www.vpro.nl/file with whitespace.html";
        String expected = "http://www.vpro.nl/file%20with%20whitespace.html";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    void normalizeDoubleEncode() {
        String input = "http://www.vpro.nl/file%20with%20whitespace.html";
        String expected = "http://www.vpro.nl/file%20with%20whitespace.html";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    void normalizeQuery() {
        String input = "http://www.vpro.nl/file.html?c=2&b=1&a";
        String expected = "http://www.vpro.nl/file.html?a&b=1&c=2";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    void normalizeQueryWithHashTag() {
        String input = "http://www.vpro.nl/file.html?c=2&b=1&a#anchor";
        String expected = "http://www.vpro.nl/file.html?a&b=1&c=2#anchor";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    void normalizePort80() {
        String input = "http://www.vpro.nl:80/file.html";
        String expected = "http://www.vpro.nl/file.html";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    void normalizePort443() {
        String input = "https://www.vpro.nl:443/file.html";
        String expected = "https://www.vpro.nl/file.html";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    // See MGNL-11633
    void strangeMid() {
        String input = "http://www-acc.human.nl/speel.Transcoding aanvraag \t\bKRO_117053.html";
        assertThat(Urls.normalize(input)).isEqualTo("http://www-acc.human.nl/speel.Transcoding%20aanvraag%20%09%08KRO_117053.html");
    }

    @Test
    void endsWithQuestionMark() {
        String input = "http://srebrenica.vpro.nl?";
        assertThat(Urls.normalize(input)).isEqualTo("http://srebrenica.vpro.nl");
    }

    @Test
    void portalFrom() {
        Portal portal = Urls.portalFrom("VPRONL", "https://www.vpro.nl/");
        assertThat(portal.getUrl()).isEqualTo("https://www.vpro.nl");
        assertThat(portal.getSection()).isNull();
    }

    @Test
    void portalFromWithDoubleSlash() {
        Portal portal = Urls.portalFrom("VPRONL", "https://www.vpro.nl//");
        assertThat(portal.getUrl()).isEqualTo("https://www.vpro.nl");
        assertThat(portal.getSection()).isNull();
    }

    @Test
    void portalFromWithFileExtension() {
        Portal portal = Urls.portalFrom("VPRONL", "https://www.vpro.nl/article.html");
        assertThat(portal.getUrl()).isEqualTo("https://www.vpro.nl");
        assertThat(portal.getSection()).isNull();
    }

    @Test
    void portalFromWithSection() {
        Portal portal = Urls.portalFrom("VPRONL", "https://www.vpro.nl/news/article.html");
        assertThat(portal.getUrl()).isEqualTo("https://www.vpro.nl");
        assertThat(portal.getSection().getPath()).isEqualTo("/news");
    }

    @Test
    void normalizeCrid() {
        String crid = "crid://cinema/Movies/123";
        String normalized = Urls.normalize(crid);
        assertThat(normalized).isEqualTo(crid);
    }

}
