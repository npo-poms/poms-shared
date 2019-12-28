package nl.vpro.domain.media.update;

import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import nl.vpro.domain.Xmlns;
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
public class PersonUpdate {

    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE)
    @Getter
    protected String givenName;

    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE)
    @Getter
    protected String familyName;

    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    protected RoleType role;

    public PersonUpdate(String givenName, String familyName, RoleType role) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.role = role;
    }

    public PersonUpdate(Person p) {
        this(p.getGivenName(), p.getFamilyName(), p.getRole());
    }

    protected PersonUpdate() {
        // needed for jaxb
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("givenName", givenName)
            .append("familyName", familyName)
            .append("role", role)
            .toString();
    }

    public Person toPerson() {
        return new Person(getGivenName(), getFamilyName(), getRole());
    }
}
