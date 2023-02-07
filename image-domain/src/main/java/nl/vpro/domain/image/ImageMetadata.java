package nl.vpro.domain.image;

import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.PolyNull;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.domain.support.License;
import nl.vpro.jackson2.Views;

/**
 *  An extended version of {@link nl.vpro.domain.image.Metadata}. Most noticeably, it adds {@link #getSourceSet()}, and also {@link #getAreaOfInterest()}
 */
public interface ImageMetadata extends Metadata {

    /**
     * The associated {@link ImageSourceSet}. This will normally be calculable from other fields.
     */
    @JsonView(Views.Model.class)
    ImageSourceSet getSourceSet();

    @Nullable
    Area getAreaOfInterest();

    /**
     * The point of interest is just the exact middle point of {@link #getAreaOfInterest()},
     * or if there is no such a thing is defined, then {@link RelativePoint#MIDDLE}
     */
    @JsonView(Views.Model.class)
    default RelativePoint getPointOfInterest() {
        Area areaOfInterest = getAreaOfInterest();
        if (areaOfInterest != null && getWidth() != null && getHeight() != null) {
            return new RelativePoint(
                50f * (areaOfInterest.getLowerLeft().getX() + areaOfInterest.getUpperRight().getX()) / getWidth(),
                50f * (areaOfInterest.getLowerLeft().getY() + areaOfInterest.getUpperRight().getY()) / getHeight()
            );
        } else {
            return RelativePoint.MIDDLE;
        }
    }

    static ImageMetadataImpl.Builder builder() {
        return ImageMetadataImpl.builder();
    }


    @PolyNull
    static <W extends Metadata> ImageMetadata of(@PolyNull W wrapped) {
        if (wrapped == null) {
            return null;
        } else {
            if (wrapped instanceof  ImageMetadata) {
                return (ImageMetadata) wrapped;
            } else {
                return new Wrapper<>(wrapped);
            }
        }
    }

    /**
     * If an image object already implements {@link Metadata}, then an interface can
     * be created using this wrapper.
     * <p>
     * This e.g. is useful for poms images {@link nl.vpro.domain.media.support.Image} and {@link nl.vpro.domain.page.Image}, which are presently not yet implementing {@link ImageMetadata}
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
    class Wrapper<W extends Metadata> implements ImageMetadata {

        @JsonIgnore
        @Getter
        final W wrapped;


        public Wrapper(W wrapped) {
            this.wrapped = wrapped;
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

        @Override
        public ImageSourceSet getSourceSet() {
            return ImageSourceService.INSTANCE.getSourceSet(wrapped);
        }
        protected void setSourceSet(ImageSourceSet ignored) {

        }

        @Override
        public @Nullable Area getAreaOfInterest() {
            if (wrapped instanceof ImageMetadata) {
                return ((ImageMetadata)wrapped).getAreaOfInterest();
            } else {
                return null;
            }
        }

        @Override
        public ImageType getType() {
            return wrapped.getType();
        }

        @Override
        public String getTitle() {
            return wrapped.getTitle();
        }

        @Override
        public String getDescription() {
            return wrapped.getDescription();
        }

        @Override
        public License getLicense() {
            return wrapped.getLicense();
        }

        @Override
        public String getSource() {
            return wrapped.getSource();
        }

        @Override
        public String getSourceName() {
            return wrapped.getSourceName();
        }

        @Override
        public String getCredits() {
            return wrapped.getCredits();
        }

        @Override
        public Integer getHeight() {
            return wrapped.getHeight();
        }

        @Override
        public Integer getWidth() {
            return wrapped.getWidth();
        }

        @Override
        @JsonProperty("lastModified")
        public Instant getLastModifiedInstant() {
            return wrapped.getLastModifiedInstant();
        }

        @Override
        @JsonProperty("creationDate")
        public Instant getCreationInstant() {
            return wrapped.getCreationInstant();
        }
    }
}
