package nl.vpro.domain;

import java.io.Serial;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlValue;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.validation.NoHtml;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractOwnedTextEntity<T extends AbstractOwnedTextEntity<T, P>, P> extends AbstractOwnedText<T> {

    @Serial
    private static final long serialVersionUID = -4621135759610402997L;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NoHtml
    @XmlValue
    protected String value;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    protected AbstractOwnedTextEntity(String value, OwnerType owner, TextualType type) {
        super(owner, type);
        this.value = value;
    }

    protected AbstractOwnedTextEntity() {
    }


    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public abstract P getParent();

    public abstract  void setParent(@NotNull P  parent);


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
                        if (!Objects.equals(ownedTextEntity.value, value)) {
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
