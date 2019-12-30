package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "targetGroupsType")
@Getter
@Setter
public class TargetGroups  extends AbstractMediaObjectOwnableList<TargetGroups, TargetGroup> {


    public TargetGroups() {}

    public static TargetGroups empty(@NonNull  OwnerType owner) {
        return TargetGroups.builder().owner(owner).build();
    }

    @lombok.Builder(builderClassName = "Builder")
    private TargetGroups(@lombok.NonNull @Singular  List<TargetGroupType> values, @lombok.NonNull OwnerType owner) {
        this.values = values.stream().map(TargetGroup::new).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }

    @Override
    @NonNull
    @XmlElement(name="targetGroup")
    @JsonIgnore
    public List<TargetGroup> getValues() {
        return values;
    }

    public void setValues(List<TargetGroup> list) {
        this.values = list;
    }
}
