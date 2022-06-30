package nl.vpro.domain.image;

import nl.vpro.validation.URI;

@URI(mustHaveScheme = true, minHostParts = 2)
public @interface ImageURL {
}
