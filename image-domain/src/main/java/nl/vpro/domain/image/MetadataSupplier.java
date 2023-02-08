package nl.vpro.domain.image;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The basic interface for images is {@link nl.vpro.domain.image.Metadata} (POMS) and its extension {@link ImageMetadata}
 * <p>
 * There are a lot of image objects lying around which cannot directly implements one of these, because of conflicting methods.
 * <p>
 * So, at least they can implement this, and have {@link #getMetadata()}
 * <p>
 *
 *
 *
 */
public interface MetadataSupplier {

    /**
     */
    @NonNull
    Metadata getMetadata();


}
