package nl.vpro.domain.image;

import nl.vpro.domain.support.License;

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

    String getSource();

    String getSourceName();

    String getCredits();

}
