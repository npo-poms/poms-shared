package nl.vpro.domain.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Singular;
import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@XmlType(name = "topicsType")
public class Topics extends AbstractMediaObjectOwnableList<Topics, Topic> {

    public static Topics empty(@NonNull OwnerType owner) {
        return Topics.builder().owner(owner).build();
    }

    public Topics() {
    }

    public Topics(@lombok.NonNull MediaObject parent, @lombok.NonNull OwnerType owner) {

        this.parent = parent;
        this.owner = owner;
    }

    @lombok.Builder(builderClassName = "Builder")
    private Topics(@lombok.NonNull @Singular List<Topic> values,
                   @lombok.NonNull OwnerType owner,
                   MediaObject parent) {

        this.values = values.stream().map(Topic::clone).collect(Collectors.toList());
        this.owner = owner;
        this.values.forEach(v -> v.setParent(this));
        this.parent = parent;
    }

    @Override
    @NonNull
    @XmlElement(name="topics")
    @JsonIgnore
    public List<Topic> getValues() {
        return values;
    }

    public void setValues(List<Topic> list) {
        values = list;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Topics clone() {
        return new Topics(values, owner, null);
    }
}
