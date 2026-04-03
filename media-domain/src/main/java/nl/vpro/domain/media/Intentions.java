package nl.vpro.domain.media;

import lombok.*;

import java.io.Serial;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.*;

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

    @Serial
    private static final long serialVersionUID = -1077914108074955136L;

    public Intentions() {}

    public static Intentions empty(@NonNull OwnerType owner) {
        return Intentions.builder().owner(owner).build();
    }

    @lombok.Builder(builderClassName = "Builder")
    private Intentions(
        @Singular List<IntentionType> values,
        OwnerType owner) {
        this.values = values.stream().map(Intention::new).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }

    /**
     * TODO: Why couldn't this have been simply a list of IntentionType?
     */
    @Override
    @NonNull
    @XmlElement(name="intention")
    @JsonProperty("values")
    public List<Intention> getFilteredValues() {
        return values;
    }

    public void setValues(List<Intention> list) {
        this.values = list;
    }

    public Intentions clone(OwnerType newOwner) {
        return new Intentions(
            values.stream()
                .map(Intention::getValue)
                .collect(Collectors.toList()),
            newOwner
        );
    }
}
