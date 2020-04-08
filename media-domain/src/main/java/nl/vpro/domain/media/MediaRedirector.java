package nl.vpro.domain.media;

import java.util.Optional;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public interface MediaRedirector  {

    Optional<String> redirect(String mid);

}
