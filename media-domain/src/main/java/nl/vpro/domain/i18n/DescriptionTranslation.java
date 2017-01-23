package nl.vpro.domain.i18n;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import nl.vpro.domain.AbstractOwnedText;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
public class DescriptionTranslation extends AbstractOwnedText<TitleTranslation> {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MediaObjectTranslation parent;

    public DescriptionTranslation(String title, OwnerType owner, TextualType type) {
        super(title, owner, type);
    }

    public DescriptionTranslation() {
    }




    public Long getId() {
        return id;
    }

    public void setId(Long  id) {
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
    @Lob
    @Type(type = "org.hibernate.type.StringType")
    public String get() {
        return super.get();

    }
}
