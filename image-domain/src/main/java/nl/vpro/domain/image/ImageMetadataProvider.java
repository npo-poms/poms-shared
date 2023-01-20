package nl.vpro.domain.image;

import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated
public interface ImageMetadataProvider extends ImageMetadataSupplier {

    @NonNull
    @Deprecated
    ImageMetadata toImageMetadataWithSourceSet();

    @Override
    @NonNull
    default ImageMetadata getImageMetadataWithSourceSet() {
        return toImageMetadataWithSourceSet();
    }

    /**
     * This has to be implemented,
     * <p>
     * Normally when using you should call {@link #getImageMetadataWithSourceSet() , which will also add  (extra)  {@link ImageMetadata#getSourceSet()} via {@link ImageSourceService}.
     */
    @NonNull
    @Deprecated
    ImageMetadata toImageMetadata();

    @Override
    @NonNull
    default ImageMetadata getImageMetadata() {
        return toImageMetadata();
    }

}
