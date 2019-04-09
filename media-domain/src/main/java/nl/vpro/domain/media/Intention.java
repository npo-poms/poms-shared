package nl.vpro.domain.media;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionType")
@Data
@EqualsAndHashCode(callSuper = false)
public class Intention extends DomainObject implements Serializable, Ownable, Child<MediaObject> {

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    private MediaObject parent;

    @Enumerated(EnumType.STRING)
    @XmlAttribute
    private OwnerType owner;

    @Enumerated(EnumType.STRING)
    @XmlAttribute(name = "type")
    private IntentionType value;


    @Column(name = "list_index", nullable = true
        // hibernate sucks incredibly
    )
    @XmlTransient
    @NotNull
    @Getter
    @Setter
    private Integer listIndex = 0;

    public Intention() {}

    @lombok.Builder(builderClassName = "Builder")
    private Intention(IntentionType value, OwnerType owner) {
        this.value = value;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intention intention = (Intention) o;
        return owner == intention.owner &&
                value == intention.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, value);
    }
}
