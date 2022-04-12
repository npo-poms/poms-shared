package nl.vpro.domain.image;

import nl.vpro.domain.Trackable;
import nl.vpro.domain.support.License;
import nl.vpro.validation.URI;

/**
 * All our known image types implement at least this. A immutable representation of its metadata.
 *
 * It does not contain any information about how to display it.
 *
 * @author Michiel Meeuwissen
 * @since 5.13
 */
public interface BasicMetaData<T extends BasicMetaData<T>> extends Trackable {

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
