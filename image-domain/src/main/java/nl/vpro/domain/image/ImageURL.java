package nl.vpro.domain.image;

import nl.vpro.validation.URI;

/**
 * {@link URI} validator for URI's that point to images.
 *
 * A valid URI, that must have a scheme, must have a host with at least two parts.
 *
 */
@URI(mustHaveScheme = true, minHostParts = 2)
public @interface ImageURL {
}
