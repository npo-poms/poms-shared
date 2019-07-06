package nl.vpro.domain.media;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

import static javax.persistence.CascadeType.ALL;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionsType")
@Getter
@Setter
public class Intentions extends DomainObject implements Serializable, Child<MediaObject>, Comparable<Intentions>, Ownable {

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    private MediaObject parent;

    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @Setter(AccessLevel.PRIVATE)
    private OwnerType owner;

    @OneToMany(cascade = {ALL})
    @JoinColumn(name = "parent_id")
    @JsonProperty("values")
    @OrderColumn(name = "list_index", nullable = true)
    @XmlElement(name="intention")
    private List<Intention> values = new ArrayList<>();

    public Intentions() {}

    @lombok.Builder(builderClassName = "Builder")
    private Intentions(@NonNull @Singular  List<IntentionType> values, @NonNull OwnerType owner) {
        this.values = values.stream().map(Intention::new).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }


    public Intentions copy() {
        return new Intentions(values.stream().map(Intention::getValue).collect(Collectors.toList()), owner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intentions intentions = (Intentions) o;
        return owner == intentions.owner &&
                values.equals(intentions.values);
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
    public int compareTo(Intentions o) {
        if (this.getOwner().equals(o.getOwner())){
            if (!Objects.equals(this.values, o.values)) {
                //order is undefined (we never expect 2 intentions with same owner in a set anyway)
                return -1;
            }
        }
        return this.getOwner().compareTo(o.getOwner());
    }

    @Override
    public String toString() {
        return "Intentions:" + owner + ":" + values;
    }
}
