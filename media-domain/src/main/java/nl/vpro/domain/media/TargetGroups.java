package nl.vpro.domain.media;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.support.OwnableR;
import nl.vpro.domain.media.support.OwnerType;

import static javax.persistence.CascadeType.ALL;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "targetGroupsType")
@Getter
@Setter
public class TargetGroups extends DomainObject implements Serializable, Child<MediaObject>, Comparable<TargetGroups>, OwnableR {

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
    @XmlElement(name="targetGroup")
    private List<TargetGroup> values;

    public TargetGroups() {}

    @lombok.Builder(builderClassName = "Builder")
    private TargetGroups(List<TargetGroup> values, OwnerType owner) {
        this.values = values;
        this.owner = owner;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetGroups targetGroups = (TargetGroups) o;
        return owner == targetGroups.owner &&
               values == targetGroups.values;
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
    public int compareTo(TargetGroups o) {
        if (this.getOwner().equals(o.getOwner())){
            if (! Objects.equals(this.values, o.values)) {
                // TODO: order is undefined!, I think compareTo(o1, o2) should be -1 * compareTo(o2, o1);
                return -1;
            }
        }
        return this.getOwner().compareTo(o.getOwner());
    }

}
