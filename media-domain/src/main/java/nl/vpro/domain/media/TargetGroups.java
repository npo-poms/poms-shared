package nl.vpro.domain.media;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;

/**
 * Collects, for a certain {@link OwnerType owner}, a list of {@link TargetGroup target groups}, which are connected to a {@link MediaObject media object}.
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "targetGroupsType")
@Getter
@Setter
@Slf4j
public class TargetGroups  extends AbstractMediaObjectOwnableList<TargetGroups, TargetGroup> {

    @Serial
    private static final long serialVersionUID = -4237232154628528835L;

    public TargetGroups() {}

    public static TargetGroups empty(@NonNull  OwnerType owner) {
        return TargetGroups.builder().owner(owner).build();
    }

    @lombok.Builder(builderClassName = "Builder")
    private TargetGroups(
        @NonNull @Singular  List<TargetGroupType> values,
        @NonNull OwnerType owner,
        MediaObject parent) {
        this.values = values.stream()
            .map(TargetGroup::new)
            .collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
        this.parent = parent;
    }


    @JsonSetter("values")
    @XmlElement(name="targetGroup")
    public void setValues(List<TargetGroup> list) {
        this.values = list;
        if (list != null) {
            list.removeIf(v -> {
                if (v == null) {
                    log.warn("Removed null value from targetgroups of {}", this.getParent());
                    return true;
                } else {
                    return false;
                }
            });
            list.forEach(v -> v.setParent(this));
        }
    }

    public TargetGroups withOwner(OwnerType owner) {
        return TargetGroups.builder()
            .values(
                values.stream()
                    .map(TargetGroup::getValue)
                    .collect(Collectors.toList())
            )
            .owner(owner)
            .parent(getParent())
            .build();
    }

     public TargetGroups withParent(MediaObject parent) {
        return TargetGroups.builder()
            .values(
                values.stream()
                    .map(TargetGroup::getValue)
                    .collect(Collectors.toList())
            )
            .owner(getOwner())
            .parent(parent)
            .build();
    }



    @Override
    @XmlElement(name="targetGroup")
    @JsonGetter("values")
    public List<TargetGroup> getFilteredValues() {
        if (owner == OwnerType.INHERITED && getParent() != null) {
            AgeRating a = getParent().getAgeRating();
            if (a != null) {
                return values.stream()
                    .filter(tg -> tg.value.getAgeRatings().contains(a))
                    .collect(Collectors.toList());
            }
        }
        return values;
    }

}
