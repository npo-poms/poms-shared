package nl.vpro.domain.image;

import java.time.Instant;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;

import nl.vpro.domain.Trackable;
import nl.vpro.domain.support.License;
import nl.vpro.validation.URI;

/**
 * All our known image types implement at least this. An immutable representation of its metadata.
 *
 * It does not contain any information about how to display it.
 *
 * @author Michiel Meeuwissen
 * @since 5.13
 * @param <T> self reference
 */
public interface Metadata<T extends Metadata<T>> extends Trackable {

    ImageType getType();

    String getTitle();

    String getDescription();

    /**
     * @since 5.32
     */
    @Beta
    default String getAlternative() {
        return null;
    }

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
     * Who or what has credits.
     *
     */
    String getCredits();


    Integer getHeight();

    Integer getWidth();


    /**
     * When making an implementation of {@link Metadata}, you can define a {@link lombok.Builder} which may
     * implement this interface.
     *
     * This provides then {@link #from(Metadata)} which is usefull to quickly filling it, using different implementations
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

        default SELF from(@Nullable Metadata<?> from) {
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
                    .credits(from.getCredits());
            } else {
                return (SELF) this;
            }
        }

    }

}
