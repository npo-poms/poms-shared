package nl.vpro.domain.gtaa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Associates a GTAA 'scheme' (see {@link Scheme}), to a java class.
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GTAAScheme {
    Scheme value();
}
