/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.security;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;

import static nl.vpro.media.odi.security.OdiAuthentication.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
public class OdiAuthenticationTest {
    private final Program data = MediaTestDataBuilder.program().withMid().build();

    @Test
    public void testHandleOnMid() {
        String mid = data.getMid();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/path/media/" + mid);
        request.addHeader("X-NPO-Date", Util.rfc822(new Date()));
        request.addHeader("X-NPO-Mid", mid);
        request.addHeader("Origin", "http://www.vpro.nl");
        request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("privateKey", Util.concatSecurityHeaders(request)));

        OdiAuthentication check = new OdiAuthentication();
        URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
        check.setConfigFolder(url.getFile().replaceAll("%20", " "));
        check.init();

        check.handleMedia(data, request);
    }

    @Test
    public void testWildcardOrigins() {
        OdiClient client = new OdiClient("public", Arrays.asList("*.vpro.nl", "localhost:*"), "", false);
        assertThat(client.matchesOrigin("www.vpro.nl")).isTrue();
        assertThat(client.matchesOrigin("localhost:8080")).isTrue();
        assertThat(client.matchesOrigin("vpro.nl")).isFalse();

    }

    @Test
    public void testExtractClientConfig() {
        OdiAuthentication check = new OdiAuthentication();
        OdiClient lineClient = check.extractClientConfig("meda:privatekey:true:http://bla:8080   ,http://*.vpro.nl, http://vpro:* ");
        OdiClient client = new OdiClient("meda", Arrays.asList("http://bla:8080", "http://*.vpro.nl", "http://vpro:*"), "privatekey", true);
        assertEquals(lineClient, client);
    }


    @Test
    public void testHandleOnMidIE89() {
        String mid = data.getMid();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/path/media/" + mid);
        Date now = new Date();

        String message = ie89Message("http://www.vpro.nl", now, mid, null);

        String ieParam = "{\"x-npo-date\" : \"" + Util.rfc822(now) + "\", " +
            "\"x-npo-mid\" : \"" + mid + "\", " +
            "\"authorization\" : \"NPO apiKey:" + Util.hmacSHA256("privateKey", message) + "\"}";


        request.addParameter("iecomp", Base64.encodeBase64String(ieParam.getBytes()));

        request.addHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)");
        request.addHeader("Origin", "http://www.vpro.nl");

        OdiAuthentication check = new OdiAuthentication();
        URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
        check.setConfigFolder(url.getFile().replaceAll("%20", " "));
        check.init();

        check.handleMedia(data, request);
    }

    @Test
    public void testHandleOnMidWithXOrigin() {
        String mid = data.getMid();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/path/media/" + mid);
        request.addHeader("X-NPO-Date", Util.rfc822(new Date()));
        request.addHeader("X-NPO-Mid", mid);
        request.addHeader("X-Origin", "file://somepath");
        request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("privateKey", Util.concatSecurityHeaders(request)));

        OdiAuthentication check = new OdiAuthentication();
        URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
        check.setConfigFolder(url.getFile().replaceAll("%20", " "));
        check.init();

        check.handleMedia(data, request);
    }

    @Test
    public void testHandleOnMidXOriginNotAllowedExplicitly() {
        assertThatThrownBy(() -> {

            String mid = data.getMid();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServletPath("/path/media/" + mid);
            request.addHeader("X-NPO-Date", Util.rfc822(new Date()));
            request.addHeader("X-NPO-Mid", mid);
            request.addHeader("X-Origin", "http://www.vpro.nl");
            request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("privateKey", Util.concatSecurityHeaders(request)));

            OdiAuthentication check = new OdiAuthentication();
            URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
            check.setConfigFolder(url.getFile().replaceAll("%20", " "));
            check.init();

            check.handleMedia(data, request);
        }).isInstanceOf(OdiAuthentication.NoAccessException.class);

    }

    @Test
    public void testHandleOnMidXOriginNotAllowed() {
        assertThatThrownBy(() -> {
            String mid = data.getMid();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServletPath("/path/media/" + mid);
            request.addHeader("X-NPO-Date", Util.rfc822(new Date()));
            request.addHeader("X-NPO-Mid", mid);
            request.addHeader("X-Origin", "http://rs.test.vpro.nl");
            request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("privateKey", Util.concatSecurityHeaders(request)));

            OdiAuthentication check = new OdiAuthentication();
            URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
            check.setConfigFolder(url.getFile().replaceAll("%20", " "));
            check.init();

            check.handleMedia(data, request);
        }).isInstanceOf(OdiAuthentication.NoAccessException.class);
    }

    @Test
    public void testHandleWrongMid() {
        assertThatThrownBy(() -> {

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("X-NPO-Date", Util.rfc822(new Date()));
            request.addHeader("X-NPO-Mid", "POMS_MISSING");
            request.addHeader("Origin", "http://www.vpro.nl");
            request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("privateKey", Util.concatSecurityHeaders(request)));

            OdiAuthentication check = new OdiAuthentication();
            URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
            check.setConfigFolder(url.getFile().replaceAll("%20", " "));
            check.init();

            check.handleMedia(data, request);
        }).isInstanceOf(OdiAuthentication.NoAccessException.class);
    }

    @Test
    public void testHandleWhenExpiredTimestamp() {
        assertThatThrownBy(() -> {

            String mid = data.getMid();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServletPath("/path/media/" + mid);
            request.addHeader("X-NPO-Date", Util.rfc822(new Date(System.currentTimeMillis() - 11 * 60 * 1000)));
            request.addHeader("X-NPO-Mid", mid);
            request.addHeader("Origin", "http://www.vpro.nl");
            request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("privateKey", Util.concatSecurityHeaders(request)));

            OdiAuthentication check = new OdiAuthentication();
            check.setExpiresInMinutes(10);
            URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
            check.setConfigFolder(url.getFile().replaceAll("%20", " "));
            check.init();

            check.handleMedia(data, request);
        }).isInstanceOf(OdiAuthentication.NoAccessException.class);
    }

    @Test
    public void testHandleWrongReferrer() {
        assertThatThrownBy(() -> {

            String mid = data.getMid();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServletPath("/path/media/" + mid);
            request.addHeader("X-NPO-Date", Util.rfc822(new Date()));
            request.addHeader("X-NPO-Mid", mid);
            request.addHeader("Origin", "http://other.site.nl/");
            request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("privateKey", Util.concatSecurityHeaders(request)));


            OdiAuthentication check = new OdiAuthentication();
            URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
            check.setConfigFolder(url.getFile().replaceAll("%20", " "));
            check.init();

            check.handleMedia(data, request);
        }).isInstanceOf(OdiAuthentication.NoAccessException.class);

    }

    @Test
    public void testHandleWrongSecret() {
        assertThatThrownBy(() -> {

            String mid = data.getMid();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setServletPath("/path/media/" + mid);
            request.addHeader("X-NPO-Date", Util.rfc822(new Date()));
            request.addHeader("X-NPO-Mid", mid);
            request.addHeader("Origin", "http://www.vpro.nl");
            request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("wrongKey", Util.concatSecurityHeaders(request)));

            OdiAuthentication check = new OdiAuthentication();
            URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
            check.setConfigFolder(url.getFile().replaceAll("%20", " "));
            check.init();

            check.handleMedia(data, request);
        }).isInstanceOf(OdiAuthentication.NoAccessException.class);
    }

    @Test
    public void testHandleOnUrl() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/path/media");
        request.setMethod("POST");
        request.addHeader("X-NPO-Date", Util.rfc822(new Date()));
        String url = "http/somehost.nl/media/";
        request.addHeader("X-NPO-Url", url);
        request.addHeader("Origin", "http://www.vpro.nl");
        request.addHeader("Authorization", "NPO apiKey:" + Util.hmacSHA256("privateKey", Util.concatSecurityHeaders(request)));

        OdiAuthentication check = new OdiAuthentication();
        URL resource = this.getClass().getResource("/nl/vpro/media/odi/security/");
        check.setConfigFolder(resource.getFile().replaceAll("%20", " "));
        check.init();

        check.handleUrl(url, request);
    }

    @Test
    public void testHandleOnUrlIE89() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/path/media");
        request.setMethod("POST");
        String source = "http/somehost.nl/media/";

        Date now = new Date();

        String message = ie89Message("http://www.vpro.nl", now, null, source);

        String ieParam = "{\"x-npo-date\" : \"" + Util.rfc822(now) + "\", " +
            "\"x-npo-url\" : \"" + source + "\", " +
            "\"authorization\" : \"NPO apiKey:" + Util.hmacSHA256("privateKey", message) + "\"}";


        request.addParameter("iecomp", Base64.encodeBase64String(ieParam.getBytes()));

        request.addHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)");
        request.addHeader("Origin", "http://www.vpro.nl");

        OdiAuthentication check = new OdiAuthentication();
        URL url = this.getClass().getResource("/nl/vpro/media/odi/security/");
        check.setConfigFolder(url.getFile().replaceAll("%20", " "));
        check.init();

        check.handleUrl(source, request);
    }

    private String ie89Message(String origin, Date date, String mid, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append("origin").append(':').append(origin);

        sb.append(',');

        sb.append(X_NPO_DATE).append(':').append(Util.rfc822(date));

        sb.append(',');

        if(mid != null) {
            sb.append(X_NPO_MID).append(':').append(mid);
        }

        if(url != null) {
            sb.append(X_NPO_URL).append(':').append(url);
        }

        return sb.toString();
    }
}

