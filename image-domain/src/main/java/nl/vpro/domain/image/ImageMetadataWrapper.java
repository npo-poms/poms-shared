package nl.vpro.domain.image;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;


public  class ImageMetadataWrapper extends MetadataWrapper implements ImageMetadata {

    @JsonIgnore
    protected final ImageMetadata wrapped;

    public ImageMetadataWrapper(ImageMetadata wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public ImageSourceSet getSourceSet() {
        return wrapped.getSourceSet();
    }

    @Override
    public @Nullable Area getAreaOfInterest() {
        return wrapped.getAreaOfInterest();
    }
}
