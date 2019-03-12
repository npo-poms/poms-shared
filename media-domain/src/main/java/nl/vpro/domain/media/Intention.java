package nl.vpro.domain.media;

import lombok.Data;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Entity
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "intention", namespace = Xmlns.MEDIA_NAMESPACE)
@Data
public class Intention implements Serializable, Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value=EnumType.STRING)
    private OwnerType owner;

    @Enumerated(value=EnumType.STRING)
    private IntentionType intentionValue;


}