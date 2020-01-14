package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Credits;
import nl.vpro.domain.media.Name;
import nl.vpro.domain.media.RoleType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nameUpdateType")
@XmlRootElement(name = "name")
@ToString(of = { "gtaaUri"}, callSuper = true)
public class NameUpdate extends CreditsUpdate {

    @XmlAttribute(required = true)
    @Getter
    private String gtaaUri;

    public NameUpdate(String gtaaUri, RoleType role) {
        super(role);
        this.gtaaUri = gtaaUri;
    }

    public NameUpdate(Name name) {
        this(name.getGtaaUri(), name.getRole());
    }

    public NameUpdate() {
        // needed for jaxb
    }

    @Override
    public Credits toCredits() {
        return Name.builder().uri(gtaaUri).role(role).build();
    }
}
