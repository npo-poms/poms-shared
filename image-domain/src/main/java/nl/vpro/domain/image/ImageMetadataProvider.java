package nl.vpro.domain.image;

import lombok.Getter;

import java.util.Map;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * The basic interface for images is {@link nl.vpro.domain.image.Metadata} (POMS) and its extension {@link ImageMetadata}
 * <p>
 * There are a lot of image objects lying around which cannot directly implements one of these, because of conflicting methods.
 * <p>
 * So, at least they can implement this, and have {@link #toImageMetadata()}.
 *
 */
public interface ImageMetadataProvider {


    @NonNull
    default ImageMetadata toImageMetadataWithSourceSet() {
        final ImageMetadata imageMetadata = toImageMetadata();
        final Map<ImageSource.Type, ImageSource> sourceSet = ImageSourceService.INSTANCE.getSourceSet(this);
        ImageMetadataImpl.Builder builder =  ImageMetadataImpl.builder()
            .from(toImageMetadata())
            .addSourceSet(sourceSet);
        return builder.build();
    }

    /**
     * This has to be implemented,
     * <p>
     * Normally when using you should call {@link #toImageMetadataWithSourceSet() , which will also add  (extra)  {@link ImageMetadata#getSourceSet()} via {@link ImageSourceService}.
     */
    @NonNull
    ImageMetadata toImageMetadata();


    @PolyNull
    static <W extends Metadata<W>> Wrapper<W> of(@PolyNull W wrapped) {
        if (wrapped == null) {
            return null;
        } else {
            return new Wrapper<>(wrapped);
        }
    }

    /**
     * If an image object already implements {@link Metadata}, then an interface can
     * be created using this wrapper.
     * <p>
     * This e.g. is useful for poms images {@link nl.vpro.domain.media.support.Image} and {@link nl.vpro.domain.page.Image}, which are presently not yet implementing {@link ImageMetadata}
     * Imp
     */
    class Wrapper<W extends Metadata<W>> implements ImageMetadataProvider {

        @Getter
        final W wrapped;

        public Wrapper(W wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        @NonNull
        public ImageMetadata toImageMetadata() {
            return ImageMetadataImpl.builder()
                .from(this.wrapped)
                .build();
        }

        public <C extends Metadata<C>> Optional<C> unwrap(Class<C> clazz) {
            if (clazz.isInstance(wrapped)) {
                return Optional.of((C) wrapped);
            } else {
                return Optional.empty();
            }
        }
    }

}
