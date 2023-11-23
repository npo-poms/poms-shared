package nl.vpro.domain.media;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * For now a marker interface to indicate that a collection needs filtering when the parent object is published.
 *
 * This used to be, and largely still is, done by Hibernate filters.
 * @since 7.10
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicationFilter {
}
