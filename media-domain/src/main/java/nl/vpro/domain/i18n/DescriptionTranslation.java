package nl.vpro.domain.i18n;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import nl.vpro.domain.AbstractOwnedTextEntity;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
public class DescriptionTranslation extends AbstractOwnedTextEntity<DescriptionTranslation, MediaObjectTranslation> {

    @Getter
    @Setter
    @ManyToOne
    @NotNull
    MediaObjectTranslation parent;

    public DescriptionTranslation(MediaObjectTranslation parent, String title, OwnerType owner, TextualType type) {
        super(title, owner, type);
        this.parent = parent;
    }


    public DescriptionTranslation() {
    }

}
