package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.ToString;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.Credits;
import nl.vpro.domain.media.Person;
import nl.vpro.domain.media.RoleType;


/**
 * Horrible copy/paste code from {@link Person}. Mainly to get it in a different namespace.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personUpdateType",
         propOrder = {"givenName",
                      "familyName"})
@XmlRootElement(name = "person")
@ToString(of = { "givenName", "familyName" }, callSuper = true)
public class PersonUpdate extends CreditsUpdate {

    @XmlAttribute
    @Getter
    private String gtaaUri;

    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE)
    @Getter
    protected String givenName;

    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE)
    @Getter
    protected String familyName;

    public PersonUpdate(String gtaaUri, RoleType role) {
        super(role);
        this.gtaaUri = gtaaUri;
    }

    public PersonUpdate(String givenName, String familyName, RoleType role) {
        super(role);
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public PersonUpdate(Person p) {

        if (p.getGtaaUri() != null) {
            this.gtaaUri = p.getGtaaUri();
        }
        else {
            this.givenName = p.getGivenName();
            this.familyName = p.getFamilyName();
        }

        this.role = p.getRole();
    }

    public PersonUpdate() {
        // needed for jaxb
    }

    @Override
    public Credits toCredits() {

        if (gtaaUri != null) {
            return Person.builder().uri(URI.create(gtaaUri)).role(role).build();
        }
        else {
            return Person.builder().givenName(givenName).familyName(familyName).role(role).build();
        }
    }
}
