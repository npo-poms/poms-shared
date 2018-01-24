package nl.vpro.domain.api;

import java.util.ArrayList;
import java.util.List;

import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.support.Tag;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
public class AbstractFormBuilder {

    protected TextMatcher textMatcher(String text, Match match) {
        return new TextMatcher(text, match);
    }

    protected SimpleTextMatcher simpleTextMatcher(String text, Match match) {
        return new SimpleTextMatcher(text, match);
    }


    protected TextMatcherList textMatchers(Match match, TextMatcher... matchers) {
        return new TextMatcherList(match, matchers);
    }

    protected ExtendedTextMatcherList extendedTextMatchers(Match match, ExtendedTextMatcher[] matchers) {
        return new ExtendedTextMatcherList(match, matchers);
    }

    protected ExtendedTextMatcherList extendedTextMatchers(Match match, Tag... text) {
        List<ExtendedTextMatcher> query = new ArrayList<>(text.length);
        for (Tag t : text) {
            query.add(extendedTextMatcher(match, t.getText()));
        }
        return new ExtendedTextMatcherList(query, Match.SHOULD);
    }

    protected TextMatcherList textMatchers(Match match, AVType... avTypes) {
        List<TextMatcher> query = new ArrayList<>(avTypes.length);
        for (AVType avType : avTypes) {
            query.add(textMatcher(avType.name(), match));
        }
        return new TextMatcherList(query, Match.SHOULD);
    }


    protected TextMatcherList textMatchers(Match match, MediaType... types) {
        List<TextMatcher> query = new ArrayList<>(types.length);
        for (MediaType type : types) {
            query.add(textMatcher(type.name(), match));
        }
        return new TextMatcherList(query, Match.MUST);
    }

    protected TextMatcherList textMatchers(Match match, AgeRating... ageRatings) {
        List<TextMatcher> query = new ArrayList<>(ageRatings.length);
        for (AgeRating ageRating : ageRatings) {
            /* AgeRating._6 is stored as "6" */
            query.add(textMatcher(ageRating.getXmlValue(), match));
        }
        return new TextMatcherList(query, Match.MUST);
    }

    protected TextMatcherList textMatchers(Match match, ContentRating... contentRatings) {
        List<TextMatcher> query = new ArrayList<>(contentRatings.length);
        for (ContentRating type : contentRatings) {
            /* ContentRating.ANGST is stored as "ANGST" */
            query.add(textMatcher(type.name(), match));
        }
        return new TextMatcherList(query, Match.MUST);
    }

    protected TextMatcher textMatcher(Match match, String text) {
        return new TextMatcher(text, match);
    }

    protected TextMatcherList textMatchers(Match match, String... text) {
        List<TextMatcher> matchers = new ArrayList<>();
        for (String t : text) {
            matchers.add(textMatcher(match, t));
        }
        return new TextMatcherList(matchers, Match.MUST);
    }

    protected ExtendedTextMatcher extendedTextMatcher(Match match, String text) {
        return new ExtendedTextMatcher(text, match);
    }

    protected ExtendedTextMatcher extendedTextMatcher(Match match, boolean caseSensitive, String text) {
        return new ExtendedTextMatcher(text, match, null, caseSensitive);
    }

    protected ExtendedTextMatcherList extendedTextMatchers(Match match, boolean caseSensitive, String... text) {
        List<ExtendedTextMatcher> matchers = new ArrayList<>();
        for (String t : text) {
            matchers.add(extendedTextMatcher(match, caseSensitive, t));
        }
        return new ExtendedTextMatcherList(matchers, Match.MUST);
    }

    protected ExtendedTextMatcherList extendedTextMatchers(Match match, String... text) {
        return extendedTextMatchers(match, true, text);
    }

    protected ExtendedTextMatcher extendedTextMatcher(Match match, StandardMatchType matchType, boolean caseSensitive, String text) {
        return new ExtendedTextMatcher(text, match, matchType, caseSensitive);
    }

    protected ExtendedTextMatcherList extendedTextMatchers(Match match, StandardMatchType matchType, boolean caseSensitive, String... text) {
        List<ExtendedTextMatcher> matchers = new ArrayList<>();
        for (String t : text) {
            matchers.add(extendedTextMatcher(match, matchType, caseSensitive, t));
        }
        return new ExtendedTextMatcherList(matchers, Match.MUST);
    }



    protected void addTextMatchers(TextMatcherList list, String... text) {
        List<TextMatcher> matchers = list.asList();
        if (matchers == null) {
            matchers = new ArrayList<>();
        }
        matchers.addAll(textMatchers(Match.MUST, text).asList());
    }
}
