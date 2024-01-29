package nl.vpro.domain.gtaa;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonTypeName;

import nl.vpro.domain.PersonInterface;

/**
 * For persons we distinguish 'given name' and 'family name'. We do this mainly because this was done traditionally like so in POMS.
 *
 */
@NoArgsConstructor
@XmlRootElement(name = "newPerson")
@XmlType(
    name = "newPersonType",
    propOrder = {
        "givenName",
        "familyName",
        "scopeNotes"
})
@GTAAScheme(Scheme.person)
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeName("person")
public final class GTAANewPerson extends AbstractGTAANewConcept implements PersonInterface {

    @Getter
    @Setter
    @XmlElement
    private String givenName;

    @Getter
    @Setter
    @XmlElement
    private String familyName;

    @lombok.Builder
    public GTAANewPerson(
        String givenName,
        String familyName,
        @lombok.Singular List<String> scopeNotes) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.scopeNotes = scopeNotes;
    }

    @Override
    public String getName() {
        return PersonInterface.super.getName();
    }

    @Override
    public Scheme getObjectType() {
        return Scheme.person;

    }
}
