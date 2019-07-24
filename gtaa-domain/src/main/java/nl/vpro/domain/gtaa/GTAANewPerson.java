package nl.vpro.domain.gtaa;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonTypeName;

import nl.vpro.domain.PersonInterface;

@NoArgsConstructor
@XmlRootElement(name = "newPerson")
@XmlType(
    name = "newPersonType",
    propOrder = {
        "givenName",
        "familyName",
        "notes"
})
@GTAAScheme(Scheme.person)
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeName("person")
public class GTAANewPerson extends AbstractGTAANewConcept implements PersonInterface {

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
        @lombok.Singular List<String> notes) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.notes = notes;
    }

    @Override
    public String getValue() {
        return PersonInterface.stringValue(givenName, familyName);
    }

    @Override
    public Scheme getObjectType() {
        return Scheme.person;

    }
}
