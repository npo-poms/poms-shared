/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * See https://jira.vpro.nl/browse/MSE-1372
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class TextUtil {

    /**
     * Reusable pattern for matching text against illegal characters
     */
    public static final Pattern ILLEGAL_PATTERN = Pattern.compile("<.*>|&#\\d{2,4};|&[\\w]{2,8};|\\u2028");

    /**
     * Checks if given text input complies to POMS standard.
     *
     * @param input
     * @return
     * @see #ILLEGAL_PATTERN for a rough check
     */
    public static boolean isValid(String input) {
        Matcher matcher = ILLEGAL_PATTERN.matcher(input);
        if(!matcher.find()) {
            return true;
        }
        return input.equals(sanitize(input));
    }

    /**
     * Replaces all line separators with a single white space character. The line separator character (\u2028) is
     * forbidden in most modern browsers. These browsers won't render any text containing this character.
     *
     * @param input
     * @return
     */
    public static String replaceLineBreaks(String input) {
        return input != null ? input.replace('\u2028', ' ') : null;
    }

    /**
     * Replaces all non breaking space characters (\u00A0) with a normal white space character.
     *
     * @param input
     * @return
     */
    public static String replaceNonBreakingSpace(String input) {
        return input != null ? input.replace('\u00A0', ' ') : null;
    }

    /**
     * Replaces all non breaking space characters (\u00A0) with a normal white space character.
     *
     * @param input
     * @return
     */
    public static String replaceHtmlEscapedNonBreakingSpace(String input) {
        return input != null ? input.replace("&nbsp;", " ") : null;
    }

    /**
     * Un-escapes all html escape characters. For example: Replaces "&amp" with "&".
     *
     * @param input
     * @return
     */
    public static String unescapeHtml(String input) {
        return input != null ? StringEscapeUtils.unescapeHtml(input.replace("&nbsp;", " ")) : null;
    }

    /**
     * Strips html like tags from the input. All content between tags, even non-html content is being removed.
     *
     * @param input
     * @return
     */
    public static String stripHtml(String input) {
        return input != null ? Jsoup.clean(input, Whitelist.none()) : null;
    }

    /**
     * Aggressively removes all tags and escaped HTML characters from the given input and replaces some characters that
     * might lead to problems for end users.
     *
     * @param input
     * @return
     */
    public static String sanitize(String input) {
        return unescapeHtml(
            stripHtml(
                replaceLineBreaks(
                    replaceNonBreakingSpace(
                        replaceHtmlEscapedNonBreakingSpace(
                            unescapeHtml(input))))));
    }
}
