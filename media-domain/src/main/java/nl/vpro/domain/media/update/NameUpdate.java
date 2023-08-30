package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.ToString;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.*;

/**
 * @see nl.vpro.domain.media.update
 * @see Name
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nameUpdateType")
@XmlRootElement(name = "name")
@ToString(of = { "gtaaUri"}, callSuper = true)
public class NameUpdate extends CreditsUpdate {

    @XmlAttribute(required = true)
    @Getter
    @nl.vpro.validation.URI(schemes = {"http", "https"}, mustHaveScheme = true, hosts = {"data.beeldengeluid.nl"}, patterns = {"http://data\\.beeldengeluid\\.nl/gtaa/\\d+"})
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
        return Name.builder()
            .uri(URI.create(gtaaUri))
            .role(role)
            .build();
    }
}
