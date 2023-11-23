package nl.vpro.domain.media;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * For now a marker interface to indicate that a collection needs filtering when the parent object is published.
 * <p>
 * This used to be, and largely still is, done by Hibernate filters.
 * <p>
 * For now, no code is actually using this annotation, but we could use it to filter collections in the future, but at least it administrates the need for filtering now.
 *
 * @since 7.10
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicationFilter {
}
