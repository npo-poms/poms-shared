/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * See https://jira.vpro.nl/browse/MSE-1372
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class TextUtil {

    Whitelist whitelist = Whitelist.none().addTags("\n");

    /**
     * Reusable pattern for matching text against illegal characters
     */
    public static final Pattern ILLEGAL_PATTERN = Pattern.compile("<.*>|&#\\d{2,4};|&[\\w]{2,8};|\\u2028");

    /**
     * Checks if given text input complies to POMS standard.
     *
     * @param input
     * @see #ILLEGAL_PATTERN for a rough check
     */
    public static boolean isValid(String input) {
        Matcher matcher = ILLEGAL_PATTERN.matcher(input);
        if(!matcher.find()) {
            return true;
        }
        return normalizeWhiteSpace(input).equals(normalizeWhiteSpace(sanitize(input)));
    }

    public static String normalizeWhiteSpace(String input) {
        if (input == null) {
            return input;
        }
        return input.trim().replaceAll("\\s+", " ");
    }


    /**
     * Replaces all line separators with a single white space character. The line separator character (\u2028) is
     * forbidden in most modern browsers. These browsers won't render any text containing this character.
     *
     * @param input
     */
    public static String replaceLineBreaks(String input) {
        return input != null ? input.replace('\u2028', ' ') : null;
    }

    /**
     * Replaces all non breaking space characters (\u00A0) with a normal white space character.
     *
     * @param input
     */
    public static String replaceNonBreakingSpace(String input) {
        return input != null ? input.replace('\u00A0', ' ') : null;
    }

    /**
     * Replaces all non breaking space characters (\u00A0) with a normal white space character.
     *
     * @param input
     */
    public static String replaceHtmlEscapedNonBreakingSpace(String input) {
        return input != null ? input.replace("&nbsp;", " ") : null;
    }

    /**
     * Un-escapes all html escape characters. For example: Replaces "&amp;amp;" with "&amp;".
     *
     * @param input
     */
    public static String unescapeHtml(String input) {
        return input != null ? StringEscapeUtils.unescapeHtml4(input.replace("&nbsp;", " ")) : null;
    }

    /**
     * Strips html like tags from the input. All content between tags, even non-html content is being removed.
     *
     * @param input
     */
    public static String stripHtml(String input) {
        return input != null ? Jsoup.clean(input, Whitelist.none()) : null;
    }

    /**
     * Aggressively removes all tags and escaped HTML characters from the given input and replaces some characters that
     * might lead to problems for end users.
     *
     * @param input
     */
    public static String sanitize(String input) {
        return unescapeHtml(
            stripHtml(
                replaceLineBreaks(
                    replaceNonBreakingSpace(
                        replaceHtmlEscapedNonBreakingSpace(
                            unescapeHtml(input)
                        )
                    )
                )
            )
        );
    }

    private static Set<Pattern> DUTCH_PARTICLES =
        new HashSet<>(
            Arrays.asList(
                getPattern("de"),
                getPattern("het"),
                getPattern("een")
                /*, "'t", "'n" ?*/
            ));
    private static Pattern getPattern(String particle) {
        return Pattern.compile("(?i)^(" + particle + ")\\b.+");
    }

    public static String getLexico(String title, Locale locale) {
        // Deze code staat ook als javascript in media-server/src/main/webapp/vpro/media/1.0/util/format.js
        if ("nl".equals(locale.getLanguage())) {
            for (Pattern particle : DUTCH_PARTICLES) {
                Matcher matcher = particle.matcher(title);
                if (matcher.matches()) {
                    int matchLength = matcher.group(1).length();
                    String start = title.substring(0, matchLength);
                    boolean uppercase = title.toUpperCase().equals(title);
                    StringBuilder b = new StringBuilder(title.substring(matchLength).trim()).append(", ").append(uppercase ? start.toUpperCase() : start.toLowerCase());
                    if (Character.isUpperCase(start.charAt(0))) {
                        b.setCharAt(0, Character.toTitleCase(b.charAt(0)));
                    }
                    return b.toString();
                }
            }
            return title;
        } else {
            return title;
        }
    }

    public static String select(String... options) {
        for(String option : options) {
            if(option != null) {
                return option;
            }
        }
        return null;
    }
}
