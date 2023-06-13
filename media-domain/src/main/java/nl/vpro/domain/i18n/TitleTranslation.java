package nl.vpro.domain.i18n;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

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
// it's in super
public class TitleTranslation extends AbstractOwnedTextEntity<TitleTranslation, MediaObjectTranslation> {

    @Serial
    private static final long serialVersionUID = -4636628305739242913L;

    @Getter
    @Setter
    @ManyToOne
    @NotNull
    MediaObjectTranslation parent;

    public TitleTranslation(MediaObjectTranslation parent, String title, OwnerType owner, TextualType type) {
        super(title, owner, type);
        this.parent = parent;
    }

    public TitleTranslation() {
    }


    @Override
    public boolean mayContainNewLines() {
        return false;
    }
}
