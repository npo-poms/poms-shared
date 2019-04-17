package nl.vpro.domain.media;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.vpro.domain.DomainObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionType")
@Data
@EqualsAndHashCode(callSuper = true)
public class Intention  extends DomainObject implements Serializable {

    @Enumerated(EnumType.STRING)
    @XmlAttribute(name = "type")
    public IntentionType value;

    @Column(name = "list_index", nullable = true)
    @XmlTransient
    @NotNull
    @Getter
    @Setter
    private Integer listIndex = 0;

    public Intention() {}

    @lombok.Builder(builderClassName = "Builder")
    public Intention(IntentionType value) {
        this.value = value;
    }

}
