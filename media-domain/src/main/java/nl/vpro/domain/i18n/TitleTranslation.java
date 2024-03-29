package nl.vpro.domain.i18n;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

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
