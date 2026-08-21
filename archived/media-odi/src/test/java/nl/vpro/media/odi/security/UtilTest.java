/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.security;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
public class UtilTest {

    @Test
    public void testSha256() {
        assertThat(Util.sha256("input")).isEqualTo("c96c6d5be8d08a12e7b5cdc1b207fa6b2430974c86803d8891675e76fd992c20");
    }

    @Test
    public void testHmacSHA256() {
        assertThat(Util.hmacSHA256("key", "input")).isEqualTo("ngiewTr4gaisInpzbD58SQ6jtK/KDF+D3/Y5O2g6cuM=");
    }

    @Test
    public void testConcatSecurityHeadersForMid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-NPO-Mid", "POMS_S_VPRO_123456");
        request.addHeader("X-NPO-Date", Util.rfc822(new Date(0)));
        request.addHeader("Origin", "http://www.vpro.nl/");

        assertThat(Util.concatSecurityHeaders(request)).isEqualTo("origin:http://www.vpro.nl/,x-npo-date:Thu, 01 Jan 1970 00:00:00 GMT,x-npo-mid:POMS_S_VPRO_123456");
    }

    @Test
    public void testConcatSecurityHeadersForUrl() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-NPO-Url", "rtsp://somehost.com");
        request.addHeader("X-NPO-Date", Util.rfc822(new Date(0)));
        request.addHeader("Origin", "http://www.vpro.nl/");

        assertThat(Util.concatSecurityHeaders(request)).isEqualTo("origin:http://www.vpro.nl/,x-npo-date:Thu, 01 Jan 1970 00:00:00 GMT,x-npo-url:rtsp://somehost.com");
    }
}
