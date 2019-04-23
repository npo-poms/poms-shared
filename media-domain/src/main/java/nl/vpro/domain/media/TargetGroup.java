package nl.vpro.domain.media;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "targetGroupType")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "parent")
public class TargetGroup extends DomainObject implements Serializable, Child<TargetGroups> {


    @ManyToOne(targetEntity = TargetGroups.class, fetch = FetchType.LAZY)
    @XmlTransient
    private TargetGroups parent;

    @Enumerated(EnumType.STRING)
    @XmlAttribute(name = "type")
    public TargetGroupType value;


    public TargetGroup() {}

    @lombok.Builder(builderClassName = "Builder")
    public TargetGroup(TargetGroupType value) {
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
        TargetGroup that = (TargetGroup) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }
}
