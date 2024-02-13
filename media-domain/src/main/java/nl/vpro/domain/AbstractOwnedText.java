package nl.vpro.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

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

    @Serial
    private static final long serialVersionUID = 8742323961769012971L;

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
        return type + ":" + (owner != null ? (owner + ":") : "") + get();
    }
}
