package nl.vpro.domain.image;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *  An extended version of {@link nl.vpro.domain.image.Metadata}. Most noticeably, it adds {@link #getSourceSet()}, and also {@link #getAreaOfInterest()}
 */
public interface ImageMetadata extends Metadata {

    /**
     * As a map, for easier reference, but the causes a bit of pleonasm in  key and value, we could also just do a set?
     */
    ImageSourceSet getSourceSet();

    @Nullable
    Area getAreaOfInterest();

    /**
     * The point of interest is just the exact middle point of {@link #getAreaOfInterest()}, or if there is no such
     * a thing is defined, then {@link RelativePoint#MIDDLE}
     */
    @JsonIgnore
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


}
