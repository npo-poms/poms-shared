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
 * So, at least they can implement this, and have {@link #getImageMetadata()}.
 * <p>
 *
 *
 *
 */
@Deprecated
public interface ImageMetadataSupplier {


    @NonNull
    default ImageMetadata getImageMetadataWithSourceSet() {
        final Map<ImageSource.Key, ImageSource> sourceSet = ImageSourceService.INSTANCE.getSourceSet(getImageMetadata());
        ImageMetadataImpl.Builder builder =  ImageMetadataImpl.ibuilder()
            .from(getImageMetadata())
            .sourceSet(sourceSet);
        return builder.build();
    }

    /**
     * This has to be implemented,
     * <p>
     * Normally when using you should call {@link #getImageMetadataWithSourceSet() , which will also add  (extra)  {@link ImageMetadata#getSourceSet()} via {@link ImageSourceService}.
     */
    @NonNull
    ImageMetadata getImageMetadata();


    @PolyNull
    static <W extends Metadata> Wrapper<W> of(@PolyNull W wrapped) {
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
    class Wrapper<W extends Metadata> implements ImageMetadataSupplier {

        @Getter
        final W wrapped;


        public Wrapper(W wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        @NonNull
        public ImageMetadata getImageMetadata() {
            ImageMetadataImpl.Builder builder = ImageMetadataImpl.ibuilder();

            // todo
            if (this.wrapped instanceof ImageMetadata) {
                builder.from((ImageMetadata) this.wrapped);
            } else {
                builder.from(this.wrapped);
            }
            return builder.build();
        }

        public <C extends Metadata> Optional<C> unwrap(Class<C> clazz) {
            if (clazz.isInstance(wrapped)) {
                return Optional.of((C) wrapped);
            } else {
                return Optional.empty();
            }
        }

        @Override
        public String toString() {
            return "supplier[" + wrapped + "]";
        }
    }

}