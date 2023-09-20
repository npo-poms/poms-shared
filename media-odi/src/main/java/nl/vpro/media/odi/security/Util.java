/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.odi.security;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.vpro.media.odi.security.OdiAuthentication.*;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
class Util {

    private static final String RFC822 = "EEE, dd MMM yyyy HH:mm:ss z";

    private Util() {
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(UTF_8));
            return toString(hash);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String hmacSHA256(String privateKey, String data) {
        try {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(privateKey.getBytes(UTF_8), "HmacSHA256");
            hmacSHA256.init(keySpec);
            return Base64.encodeBase64String(hmacSHA256.doFinal(data.getBytes())).trim();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String concatSecurityHeaders(HttpServletRequest request) {
        final String date = request.getHeader(X_NPO_DATE);
        final String mid = request.getHeader(X_NPO_MID);
        final String url = request.getHeader(X_NPO_URL);
        String origin = request.getHeader("origin");
        if(origin == null) {
            origin = request.getHeader(X_ORIGIN);
        }

        // Order by key!
        StringBuilder sb = new StringBuilder();
        sb.append("origin").append(':').append(trimAndReplaceNull(origin));

        sb.append(',');

        sb.append(X_NPO_DATE).append(':').append(trimAndReplaceNull(date));

        sb.append(',');

        if(mid != null) {
            sb.append(X_NPO_MID).append(':').append(trimAndReplaceNull(mid));
        }

        if(url != null) {
            sb.append(X_NPO_URL).append(':').append(trimAndReplaceNull(url));
        }


        return sb.toString();
    }

    public static String rfc822(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(RFC822, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static Date rfc822(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(RFC822, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.parse(date);
    }

    private static String toString(byte[] hash) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private static String trimAndReplaceNull(String optional) {
        return (StringUtils.isNotBlank(optional) ? optional.trim() : UUID.randomUUID().toString());
    }
}
