package nl.vpro.domain.media.gtaa;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.PersonInterface;

@Data
@NoArgsConstructor
@XmlRootElement(name = "newPerson")
@GTAAScheme(Schemes.PERSOONSNAMEN)
public class GTAANewPerson implements PersonInterface, NewThesaurusObject<GTAAPerson> {

    @Getter
    @Setter
    private String givenName;

    @Getter
    @Setter
    private String familyName;

    @Getter
    @Setter
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
