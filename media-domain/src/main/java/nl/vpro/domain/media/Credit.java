package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;

/**
 * //
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@XmlTransient
public abstract class Credit extends DomainObject implements Child<MediaObject>  {


    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected RoleType role = RoleType.UNDEFINED;
}
