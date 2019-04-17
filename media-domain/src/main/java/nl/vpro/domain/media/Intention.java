package nl.vpro.domain.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

import static javax.persistence.CascadeType.ALL;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionType")
@Data
@EqualsAndHashCode(callSuper = false)
public class Intention extends DomainObject implements Serializable, Child<MediaObject>, Comparable<Intention> {

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    private MediaObject parent;

    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @Setter(AccessLevel.PRIVATE)
    private OwnerType owner;

    @ManyToMany(cascade = {ALL})
    @JsonProperty("values")
    @Column(name = "list_index", nullable = true)
    @XmlElement
    private Set<IntentionType> values;

    public Intention() {}

    @lombok.Builder(builderClassName = "Builder")
    private Intention(Set<IntentionType> values, OwnerType owner) {
        this.values = values;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intention intention = (Intention) o;
        return owner == intention.owner &&
                values == intention.values;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, values);
    }

    /**
     *  Just ensuring the comparator match equality.
     *  Once owner is ordered we don't really
     *  care about values ordering.
     *  Order of the value List should be enforced somewhere else
     */
    @Override
    public int compareTo(Intention o) {
        if (this.getOwner().equals(o.getOwner())){
            if (!this.values.equals(o.values)) {
                return -1;
            }
        }
        return this.getOwner().compareTo(o.getOwner());
    }

}
