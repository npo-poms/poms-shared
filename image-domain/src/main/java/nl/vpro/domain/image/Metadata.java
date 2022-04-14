package nl.vpro.domain.image;

import java.time.Instant;

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

    License getLicense();

    /**
     * Some URI describing where this image was original coming from
     */
    @URI
    String getSource();

    /**
     * A description for the source of this image.
     */
    String getSourceName();

    /**
     *
     */
    String getCredits();


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

        SELF lastModified(Instant lastModifiedInstant);
        SELF creationDate(Instant createInstant);
        SELF type(ImageType type);
        SELF title(String title);
        SELF description(String description);
        SELF license(License license);
        SELF source(String source);
        SELF sourceName(String sourceName);
        SELF credits(String credits);

        default SELF from(Metadata<?> from) {
            return lastModified(from.getLastModifiedInstant())
                .creationDate(from.getCreationInstant())
                .type(from.getType())
                .title(from.getTitle())
                .description(from.getDescription())
                .license(from.getLicense())
                .source(from.getSource())
                .sourceName(from.getSourceName())
                .credits(from.getCredits());
        }

    }

}
