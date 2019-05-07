package nl.vpro.domain.media.gtaa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GTAAScheme {
    String value();
}
