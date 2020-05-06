package nl.vpro.domain.api.media;

import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.api.TextMatcher;
import nl.vpro.domain.api.TextMatcherList;
import nl.vpro.domain.media.MediaRedirector;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@FunctionalInterface
public interface Redirector extends MediaRedirector {

    RedirectList redirects();

    @Override
    default Optional<String> redirect(String mid) {
        return redirects().redirect(mid);
    }

    default Map<String, String> redirectTextMatchers(
        @Nullable TextMatcherList list) {
        Map<String, String> redirects = new HashMap<>();;
        redirectTextMatchers(list, redirects);
        return redirects;
    }
    default void redirectTextMatchers(
        @Nullable TextMatcherList list, Map<String, String> redirects) {
         if (list == null) {
            return;
        }
        List<TextMatcher> l = list.asList();
        for (int i = 0; i < l.size(); i++) {
            final TextMatcher matcher = l.get(i);
            Optional<String> redirect = redirect(matcher.getValue());
            final int fi = i;
            redirect.ifPresent((r) -> {
                TextMatcher newMatcher = new TextMatcher(r, matcher.getMatch());
                newMatcher.setMatchType(matcher.getMatchType());
                l.set(fi, newMatcher);
                redirects.put(matcher.getValue(), redirect.get());
            });
        }
    }

    default  Map<String, String> redirectMediaSearch(MediaSearch search) {
         Map<String, String> redirects = new HashMap<>();
        if (search == null) {
            return redirects;
        }
        redirectTextMatchers(search.getMediaIds(), redirects);
        redirectTextMatchers(search.getDescendantOf(), redirects);
        redirectTextMatchers(search.getEpisodeOf(), redirects);
        redirectTextMatchers(search.getMemberOf(), redirects);
        return redirects;
    }


}
