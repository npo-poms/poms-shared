package nl.vpro.domain.image;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.*;

/**
 * The basic interface for images is {@link nl.vpro.domain.image.Metadata} (POMS) and its extension {@link ImageMetadata}
 * <p>
 * There are a lot of image objects lying around which cannot directly implements one of these, because of conflicting methods.
 * <p>
 * So, at least they can implement this, and have {@link #getImageMetadata()}.
 * <p>
 *
 *
 *
 */
public interface ImageMetadataSupplier {


    @NonNull
    default ImageMetadata getImageMetadataWithSourceSet() {
        final ImageMetadata imageMetadata = getImageMetadata();
        final Map<ImageSource.Key, ImageSource> sourceSet = ImageSourceService.INSTANCE.getSourceSet(imageMetadata);
        ImageMetadataImpl.Builder builder =  ImageMetadataImpl.builder()
            .from(getImageMetadata())
            .addSourceSet(sourceSet);
        return builder.build();
    }

    /**
     * This has to be implemented,
     * <p>
     * Normally when using you should call {@link #getImageMetadataWithSourceSet() , which will also add  (extra)  {@link ImageMetadata#getSourceSet()} via {@link ImageSourceService}.
     */
    @NonNull
    ImageMetadata getImageMetadata();


}
