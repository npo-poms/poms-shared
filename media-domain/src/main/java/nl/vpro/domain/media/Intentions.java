package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Giorgio Vinci
 * @since 5.11
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionsType")
@Getter
@Setter
@JsonPropertyOrder({
    "owner",
    "values"
})
public class Intentions extends AbstractMediaObjectOwnableList<Intentions, Intention> {


    public Intentions() {}

    @lombok.Builder(builderClassName = "Builder")
    private Intentions(@lombok.NonNull @Singular  List<IntentionType> values, @lombok.NonNull OwnerType owner) {
        this.values = values.stream().map(Intention::new).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }


    @Override
    @NonNull
    @XmlElement(name="intention")
    @JsonIgnore
    public List<Intention> getValues() {
        return values;
    }

    public void setValues(List<Intention> list) {
        this.values = list;
    }

    @Override
    public Intentions clone() {
        return new Intentions(values.stream().map(Intention::getValue).collect(Collectors.toList()), owner);
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

}
