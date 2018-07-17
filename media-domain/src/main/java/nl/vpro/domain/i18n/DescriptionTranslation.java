package nl.vpro.domain.i18n;

import javax.persistence.Entity;

import nl.vpro.domain.AbstractOwnedTextEntity;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
public class DescriptionTranslation extends AbstractOwnedTextEntity<DescriptionTranslation, MediaObjectTranslation> {

    public DescriptionTranslation(MediaObjectTranslation parent, String title, OwnerType owner, TextualType type) {
        super(parent, title, owner, type);
    }


    public DescriptionTranslation() {
    }
}
