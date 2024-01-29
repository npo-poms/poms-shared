package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.ToString;

import java.net.URI;

import jakarta.validation.constraints.*;
import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.*;
import nl.vpro.validation.WeakWarningValidatorGroup;


/**
 * Copy/paste code from {@link Person}. Mainly to get it in a different namespace.
 *
 * @see nl.vpro.domain.media.update
 * @see nl.vpro.domain.media.Person
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
    @NotNull(groups = WeakWarningValidatorGroup.class)
    @nl.vpro.validation.URI(schemes = {"http"}, mustHaveScheme = true, hosts = {"data.beeldengeluid.nl"}, patterns = {"http://data\\.beeldengeluid\\.nl/gtaa/\\d+"})
    private String gtaaUri;

    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE)
    @Getter
    @Null(groups = WeakWarningValidatorGroup.class)
    protected String givenName;

    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE)
    @Getter
    @Null(groups = WeakWarningValidatorGroup.class)
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
        } else {
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
            return Person.builder()
                .uri(URI.create(gtaaUri))
                .role(role)
                .build();
        } else {
            return Person.builder()
                .givenName(givenName)
                .familyName(familyName)
                .role(role)
                .build();
        }
    }
}
