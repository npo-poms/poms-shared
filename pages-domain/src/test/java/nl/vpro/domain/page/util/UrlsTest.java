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
public class UrlsTest {

    @Test
    public void testNormalizeEndingSlash() {
        String input = "http://www.vpro.nl/";
        String expected = "http://www.vpro.nl";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    public void testNormalizeEndingSlashWithQuery() {
        String input = "http://www.vpro.nl/folder/?a";
        String expected = "http://www.vpro.nl/folder?a";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    public void testNormalizeEndingSlashWithHashTag() {
        String input = "http://www.vpro.nl/folder/#/hashtag";
        String expected = "http://www.vpro.nl/folder#/hashtag";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    public void testNormalizeEncode() {
        String input = "http://www.vpro.nl/file with whitespace.html";
        String expected = "http://www.vpro.nl/file%20with%20whitespace.html";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    public void testNormalizeDoubleEncode() {
        String input = "http://www.vpro.nl/file%20with%20whitespace.html";
        String expected = "http://www.vpro.nl/file%20with%20whitespace.html";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    public void testNormalizeQuery() {
        String input = "http://www.vpro.nl/file.html?c=2&b=1&a";
        String expected = "http://www.vpro.nl/file.html?a&b=1&c=2";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    public void testNormalizeQueryWithHashTag() {
        String input = "http://www.vpro.nl/file.html?c=2&b=1&a#anchor";
        String expected = "http://www.vpro.nl/file.html?a&b=1&c=2#anchor";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    public void testNormalizePort80() {
        String input = "http://www.vpro.nl:80/file.html";
        String expected = "http://www.vpro.nl/file.html";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    public void testNormalizePort443() {
        String input = "https://www.vpro.nl:443/file.html";
        String expected = "https://www.vpro.nl/file.html";

        assertThat(Urls.normalize(input)).isEqualTo(expected);
    }

    @Test
    // See MGNL-11633
    public void testStrangeMid() {
        String input = "http://www-acc.human.nl/speel.Transcoding aanvraag \t\bKRO_117053.html";
        assertThat(Urls.normalize(input)).isEqualTo("http://www-acc.human.nl/speel.Transcoding%20aanvraag%20%09%08KRO_117053.html");
    }

    @Test
    public void testEndsWithQuestionMark() {
        String input = "http://srebrenica.vpro.nl?";
        assertThat(Urls.normalize(input)).isEqualTo("http://srebrenica.vpro.nl");
    }

    @Test
    public void testPortalFrom() {
        Portal portal = Urls.portalFrom("VPRONL", "https://www.vpro.nl/");
        assertThat(portal.getUrl()).isEqualTo("https://www.vpro.nl");
        assertThat(portal.getSection()).isNull();
    }

    @Test
    public void testPortalFromWithDoubleSlash() {
        Portal portal = Urls.portalFrom("VPRONL", "https://www.vpro.nl//");
        assertThat(portal.getUrl()).isEqualTo("https://www.vpro.nl");
        assertThat(portal.getSection()).isNull();
    }

    @Test
    public void testPortalFromWithFileExtension() {
        Portal portal = Urls.portalFrom("VPRONL", "https://www.vpro.nl/article.html");
        assertThat(portal.getUrl()).isEqualTo("https://www.vpro.nl");
        assertThat(portal.getSection()).isNull();
    }

    @Test
    public void testPortalFromWithSection() {
        Portal portal = Urls.portalFrom("VPRONL", "https://www.vpro.nl/news/article.html");
        assertThat(portal.getUrl()).isEqualTo("https://www.vpro.nl");
        assertThat(portal.getSection().getPath()).isEqualTo("/news");
    }

    @Test
    public void testNormalizeCrid() {
        String crid = "crid://cinema/Movies/123";
        String normalized = Urls.normalize(crid);
        assertThat(normalized).isEqualTo(crid);
    }

}
