package nl.vpro.domain.media;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionType")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "parent")
public class Intention extends DomainObject implements Serializable, Child<Intentions> {


    @ManyToOne(targetEntity = Intentions.class, fetch = FetchType.LAZY)
    @XmlTransient
    private Intentions parent;

    @Enumerated(EnumType.STRING)
    @XmlAttribute(name = "type")
    public IntentionType value;


    public Intention() {}

    @lombok.Builder(builderClassName = "Builder")
    public Intention(IntentionType value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Intention intention = (Intention) o;
        return value == intention.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
