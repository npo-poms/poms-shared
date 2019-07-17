package nl.vpro.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import org.checkerframework.checker.nullness.qual.NonNull;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractOwnedText<T extends AbstractOwnedText> implements  OwnedText, Serializable {


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute(required = true)
    @Getter
    @Setter
    @NonNull
    protected OwnerType owner = OwnerType.BROADCASTER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Getter
    @Setter
    protected TextualType type;

    protected AbstractOwnedText() {
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
    @Override
    @NonNull
    public final String toString() {
        return get();
    }




}
