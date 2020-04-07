package nl.vpro.domain.api.media;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@FunctionalInterface
public interface Redirector  {

    RedirectList redirects();

}
