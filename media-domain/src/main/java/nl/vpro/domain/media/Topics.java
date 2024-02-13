package nl.vpro.domain.media;

import lombok.Singular;

import java.io.Serial;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;

@Entity
@XmlType(name = "topicsType")
public class Topics extends AbstractMediaObjectOwnableList<Topics, Topic> {

    @Serial
    private static final long serialVersionUID = -1309416187665166955L;

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
    private Topics(@NonNull @Singular List<Topic> values,
                   @NonNull OwnerType owner,
                   MediaObject parent) {

        this.values = values.stream().map(Topic::clone).collect(Collectors.toList());
        this.owner = owner;
        this.values.forEach(v -> v.setParent(this));
        this.parent = parent;
    }

    @Override
    @NonNull
    @XmlElement(name="topic")
    @JsonIgnore
    public List<Topic> getValues() {
        return values;
    }

    public void setValues(List<Topic> list) {
        values = list;
    }

}
