package nl.vpro.domain;

import java.io.Serial;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractOwnedTextEntity<T extends AbstractOwnedTextEntity<T, P>, P> extends AbstractOwnedText<T> implements Child<P> {

    @Serial
    private static final long serialVersionUID = -4621135759610402997L;



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    protected AbstractOwnedTextEntity(OwnerType owner, TextualType type) {
        super(owner, type);
    }

    protected AbstractOwnedTextEntity() {
    }



    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public boolean equals(Object o) {
        if (this.getClass().isInstance(o)) {
            AbstractOwnedTextEntity<T, P> ownedTextEntity = (AbstractOwnedTextEntity) o;
            if (ownedTextEntity.id != null && id != null) {
                return Objects.equals(ownedTextEntity.id, id);
            } else {
                P parent = getParent();
                if (ownedTextEntity.getParent() != null && parent != null) {
                    if (!Objects.equals(ownedTextEntity.getParent(), parent)) {
                        // different parents, we require equals value too!
                        if (!Objects.equals(ownedTextEntity.get(), get())) {
                            return false;
                        }
                    }
                }
                return Objects.equals(ownedTextEntity.owner, owner) && Objects.equals(ownedTextEntity.type, type);
            }
        } else {
            return false;
        }
    }
}
