package nl.vpro.domain.media;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionType")
@Data
@EqualsAndHashCode(callSuper = true)
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

}
