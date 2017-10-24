package nl.vpro.rs.thesaurus.update;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.PersonInterface;

@Data
@NoArgsConstructor
@XmlRootElement
public class NewPerson implements PersonInterface {

    @Getter
    @Setter
    private String givenName;

    @Getter
    @Setter
    private String familyName;

    @Getter
    @Setter
    private String note;

    @lombok.Builder
    public NewPerson(String givenName, String familyName, String note) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.note = note;
    }

    @Override
    public String getGtaaUri() {
        return null;
    }
}
