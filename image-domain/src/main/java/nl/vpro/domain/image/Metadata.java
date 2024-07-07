package nl.vpro.domain.image;

import java.time.Instant;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.*;
import com.google.common.annotations.Beta;

import nl.vpro.domain.Trackable;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.Views;
import nl.vpro.validation.CRID;
import nl.vpro.validation.URI;

/**
 * All our known image types implement at least this. An immutable representation of its metadata.
 * <p>
 * It does not contain any information about how to display it.
 *
 * @author Michiel Meeuwissen
 * @since 5.13
 * @see ImageMetadata
 */
public interface Metadata extends Trackable {

    /**
     * Specifies what type this image represents, e.g. a {@link ImageType#LOGO} or a {@link ImageType#STILL}
     */
    ImageType getType();

    String getTitle();

    String getDescription();

    /**
     * An alternative representation of the image. If the image is not viewable. Like the html alt-attribute.
     * @since 5.32
     */
    @Beta
    default String getAlternative() {
        return null;
    }

    @JsonView(Views.Model.class)
    default String getAlternativeOrTitle() {
        return Optional.ofNullable(getAlternative()).orElse(getTitle());
    }


    /**
     * The {@link License license} for the image.
     */
    License getLicense();

    /**
     * Some URI describing where this image was original coming from.
     */
    @URI
    String getSource();

    /**
     * A description for the source of this image.
     */
    String getSourceName();

    /**
     * Who or what has credits for this image.
     */
    String getCredits();


    /**
     * Height in pixels
     */
    Integer getHeight();

    /**
     * Width in pixels
     */
    Integer getWidth();

    /**
     * Height and width wrapped in a {@link Dimension}
     */
    @JsonIgnore
    default Dimension getDimension(){
        return Dimension.of(getWidth(), getHeight());
    }

    /**
     * (Alternative) identifiers for the image.
     * @since 5.34
     */
    default List<@CRID String> getCrids() {
        return Collections.emptyList();
    }

    @Nullable
    default Area getAreaOfInterest() {
        return null;
    }

    /**
     * When making an implementation of {@link Metadata}, you can define a {@link lombok.Builder} which may
     * implement this interface.
     * <p>
     * This provides then {@link #from(Metadata)} which is useful to quickly filling it, using different implementations
     *
     * @since 5.32
     */
    @Beta
    interface LombokBuilder<SELF extends LombokBuilder<SELF>> {

        @JsonProperty("lastModified")
        SELF lastModifiedInstant(Instant lastModifiedInstant);
        @JsonProperty("creationDate")
        SELF creationInstant(Instant creationInstant);
        SELF type(ImageType type);
        SELF title(String title);
        SELF description(String description);
        SELF alternative(String alternative);
        SELF license(License license);
        SELF source(String source);
        SELF sourceName(String sourceName);
        SELF credits(String credits);
        SELF height(Integer height);
        SELF width(Integer width);
        SELF crids(List<@CRID String> crids);

         default SELF longHeight(Long height) {
            return height(height == null ? null : height.intValue());
        }
        default  SELF longWidth(Long width){
            return width(width == null ? null : width.intValue());
        }

        default SELF dimensions(int width, int height) {
            return width(width)
                .height(height);
        }
        default SELF dimension(Dimension dimension) {
            return longWidth(dimension.getWidth())
                .longHeight(dimension.getHeight());
        }

        default SELF from(@Nullable Metadata from) {
            if (from != null) {

                return lastModifiedInstant(from.getLastModifiedInstant())
                    .creationInstant(from.getCreationInstant())
                    .type(from.getType())
                    .title(from.getTitle())
                    .description(from.getDescription())
                    .alternative(from.getAlternative())
                    .license(from.getLicense())
                    .source(from.getSource())
                    .sourceName(from.getSourceName())
                    .credits(from.getCredits())
                    .height(from.getHeight())
                    .width(from.getWidth())
                    .crids(from.getCrids())

                    ;
            } else {
                return (SELF) this;
            }
        }

    }



}
