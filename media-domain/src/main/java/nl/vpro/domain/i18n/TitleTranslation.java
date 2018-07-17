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
public class TitleTranslation extends AbstractOwnedTextEntity<TitleTranslation, MediaObjectTranslation> {

    public TitleTranslation(MediaObjectTranslation parent, String title, OwnerType owner, TextualType type) {
        super(parent, title, owner, type);
    }

    public TitleTranslation() {
    }
}
