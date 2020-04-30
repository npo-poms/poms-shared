package nl.vpro.domain;

import lombok.*;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

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
public abstract class AbstractOwnedText<T extends AbstractOwnedText<T>> implements OwnedText, Serializable {


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
        return type + ":" + get();
    }
}
