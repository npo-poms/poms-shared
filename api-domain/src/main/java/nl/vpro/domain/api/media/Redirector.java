package nl.vpro.domain.api.media;

import java.util.Optional;

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
}
