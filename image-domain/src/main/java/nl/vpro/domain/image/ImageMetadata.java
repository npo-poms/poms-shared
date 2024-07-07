package nl.vpro.domain.image;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.PolyNull;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.jackson2.Views;

/**
 *  An extended version of {@link nl.vpro.domain.image.Metadata}. Most noticeably, it adds {@link #getSourceSet()} which may be calculated from other properties.
 * @see Picture
 * @see PictureMetadata
 */
public interface ImageMetadata extends Metadata {

    /**
     * The associated {@link ImageSourceSet}. This will normally be calculable from other fields, and implemented using {@link ImageSourceService#getSourceSet(Metadata)}.
     * <p />
     * A {@link Picture} can be obtained via {@link ImageSourceSet#getPicture()}
     */
    @JsonView(Views.Model.class)
    ImageSourceSet getSourceSet();

    /**
     * Null safe shortcut to {@link #getSourceSet()}.{@link ImageSourceSet#getPicture() getPicture()}
     */
    @Nullable
    @JsonIgnore
    //@JsonView(Views.Model.class)
    default Picture getPicture() {
        ImageSourceSet set = getSourceSet();
        return set == null ? null : set.getPicture();
    }
    /**
     * The point of interest is just the exact middle point of {@link #getAreaOfInterest()},
     * or if there is no such a thing is defined, then {@link RelativePoint#MIDDLE}
     */
    @JsonView(Views.Model.class)
    default RelativePoint getPointOfInterest() {
        return Area.relativeCenter(
            getAreaOfInterest(), getDimension()
        );
    }


    static ImageMetadataImpl.Builder builder() {
        return ImageMetadataImpl.ibuilder();
    }


    @PolyNull
    static <W extends Metadata> ImageMetadata of(@PolyNull W wrapped) {
        if (wrapped == null) {
            return null;
        } else {
            if (wrapped instanceof  ImageMetadata imageMetadata) {
                return imageMetadata;
            } else {
                return new Wrapper<>(wrapped);
            }
        }
    }

    @PolyNull
    static <W extends MetadataSupplier> ImageMetadata of(@PolyNull W wrapped) {
        if (wrapped == null) {
            return null;
        } else {
            if (wrapped instanceof  ImageMetadata imageMetadata) {
                return imageMetadata;
            } else {
                return new Wrapper<>(wrapped);
            }
        }
    }

    /**
     * If an image object already implements {@link Metadata}, then an interface can
     * be created using this wrapper.
     * <p>
     * This e.g. is useful for poms images {@code nl.vpro.domain.media.support.Image} and {@code nl.vpro.domain.page.Image}, which are presently not yet implementing {@link ImageMetadata}
     * Imp
     */
    @JsonPropertyOrder(
    {
        "type",
        "title",
        "height",
        "width",
        "sourceSet",
        "crids",
        "areaOfInterest",
        "lastModified",
        "creationDate"
    }
    )
    class Wrapper<W extends Metadata> extends MetadataWrapper implements ImageMetadata {

        protected final MetadataSupplier supplier;

        protected Wrapper() {
            super(MetadataImpl.builder().build());
            this.supplier = null;
        }

        public Wrapper(W wrapped) {
            super(wrapped);
            this.supplier = null;
        }

        public Wrapper(MetadataSupplier wrapped) {
            super(wrapped.getMetadata());
            this.supplier = wrapped;
        }

        @Override
        public String toString() {
            return "wrapper[" + wrapped + "]";
        }

        @Override
        @JsonView(Views.Model.class)
        public ImageSourceSet getSourceSet() {
            if (supplier == null) {
                return ImageSourceService.INSTANCE.getSourceSet(wrapped);
            } else {
                return ImageSourceService.INSTANCE.getSourceSet(supplier, wrapped);
            }
        }
        protected void setSourceSet(ImageSourceSet ignored) {

        }

    }
}
