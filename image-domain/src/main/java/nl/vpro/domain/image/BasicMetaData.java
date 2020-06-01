package nl.vpro.domain.image;

import nl.vpro.domain.support.License;
import nl.vpro.validation.URI;

/**
 * All our known image types implement at least this.
 *
 * @author Michiel Meeuwissen
 * @since 5.13
 */
public interface BasicMetaData<T extends BasicMetaData<T>> {

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

}
