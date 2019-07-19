package nl.vpro.domain.gtaa;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import nl.vpro.domain.PersonInterface;

@NoArgsConstructor
@XmlRootElement(name = "newPerson")
@GTAAScheme(Scheme.PERSOONSNAMEN)
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeName("person")
public class GTAANewPerson implements PersonInterface, NewThesaurusObject<GTAAPerson> {

    @Getter
    @Setter
    @XmlElement
    private String givenName;

    @Getter
    @Setter
    @XmlElement
    private String familyName;

    @Getter
    @Setter
    @XmlElement(name = "note")
    @JsonProperty("notes")
    private List<String> notes;

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
    public String getGtaaUri() {
        return null;
    }

    @Override
    public String getValue() {
        return this.givenName + " " + this.familyName;

    }
}
