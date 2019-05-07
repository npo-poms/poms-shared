package nl.vpro.domain.media.gtaa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Associates a GTAA 'scheme' (see {@link Schemes}), to a java class.
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GTAAScheme {
    String value();
}
