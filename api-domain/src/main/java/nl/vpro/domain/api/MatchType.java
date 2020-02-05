package nl.vpro.domain.api;

import java.util.regex.Pattern;

/**
 * @author Rico Jansen
 * @since 2.3
 */


public interface MatchType {

    Pattern REGEX_PATTERN =  Pattern.compile("^(?:[\\s.?+*|{}\\[\\]\"\\\\#@&<>~]+)|(?:\\(.*[*+?{}].*\\))");

    String WILDCARD_HOLDER = "__WILDCARD__";

    default Pattern getWildcardPattern(String wildcard, boolean caseSensitive) {
        String quotedPattern = wildcard.replace("*", WILDCARD_HOLDER);
        return Pattern.compile(quotedPattern.replace(WILDCARD_HOLDER, ".*"), caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
    }

    default String getName() {
        return null;
    }

    boolean eval(String value, String input, boolean caseSensitive);

    Validation valid(String value);

    class Validation {
        final boolean valid;
        final String message;

        private Validation(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static Validation valid() {
            return new Validation(true, null);
        }

        public static Validation invalid(String message) {
            return new Validation(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }


}

