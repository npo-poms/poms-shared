package nl.vpro.domain.i18n;

import javax.persistence.*;

import nl.vpro.domain.AbstractOwnedText;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
public class TitleTranslation extends AbstractOwnedText<TitleTranslation> {

    @ManyToOne
    private MediaObjectTranslation parent;

    @Id
    @GeneratedValue
    private Long id;

    public TitleTranslation(String title, OwnerType owner, TextualType type) {
        super(title, owner, type);
    }

    public TitleTranslation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MediaObjectTranslation getParent() {
        return parent;
    }

    public void setParent(MediaObjectTranslation parent) {
        this.parent = parent;
    }

    @Override
    @Column(name = "value")
    public String get() {
        return super.get();

    }
}
