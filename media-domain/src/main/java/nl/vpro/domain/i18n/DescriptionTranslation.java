package nl.vpro.domain.i18n;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.AbstractDescriptionEntity;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
// it's in super
@Setter
@Entity
public class DescriptionTranslation extends AbstractDescriptionEntity<DescriptionTranslation, MediaObjectTranslation> {

    @Serial
    private static final long serialVersionUID = 8768731191821945231L;

    @Getter
    @ManyToOne
    @NotNull
    MediaObjectTranslation parent;

    public DescriptionTranslation(MediaObjectTranslation parent, @NonNull String description, OwnerType owner, TextualType type) {
        super(description, owner, type);
        this.parent = parent;
    }


    public DescriptionTranslation() {
    }

}
