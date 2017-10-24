package nl.vpro.rs.thesaurus.update;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * {"person":{"familyName":"dasdasdsa","givenName":"adasdsad","note":"asdasda"},"jws":"eyJMHuA"}
 */
@Data
@NoArgsConstructor
@XmlRootElement
public class NewPersonRequest {

    @NotNull
    private NewPerson person;
    @NotNull
    private String jws;


    @lombok.Builder
    private NewPersonRequest(@NotNull NewPerson person, @NotNull String jws) {
        this.person = person;
        this.jws = jws;
    }
}
