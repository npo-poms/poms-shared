package nl.vpro.domain.media;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

@EqualsAndHashCode(callSuper = true)
@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "intention")
@Data
public class Intention extends DomainObject implements Serializable, Ownable, Child<MediaObject> {


    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    private MediaObject parent;

    @Enumerated(EnumType.STRING)
    private OwnerType owner;

    @Enumerated(EnumType.STRING)
    private IntentionType value;


    @Column(name = "list_index", nullable = true
        // hibernate sucks incredibly
    )
    @XmlTransient
    @NotNull
    @Getter
    @Setter
    private Integer listIndex = 0;




}
