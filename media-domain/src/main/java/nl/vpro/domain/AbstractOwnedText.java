package nl.vpro.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@MappedSuperclass
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractOwnedText<T extends AbstractOwnedText> implements  OwnedText, Serializable {


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute(required = true)
    protected OwnerType owner = OwnerType.BROADCASTER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    protected TextualType type;


    protected AbstractOwnedText(OwnerType owner, TextualType type) {
        this.owner = owner;
        this.type = type;
    }

    protected AbstractOwnedText() {
    }

    @Override
    public TextualType getType() {
        return type;

    }

    @Override
    public void setType(TextualType type) {
        this.type = type;
    }

    @Override
    public OwnerType getOwner() {
        return owner;

    }

    @Override
    public void setOwner(OwnerType owner) {
        this.owner = owner;

    }



    @Override
    public int hashCode() {
        int result = (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Please implement equals!");
    }




}
