package nl.vpro.domain.gtaa;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonTypeName;


/**
 * @author Eric Kuijt?
 */
@NoArgsConstructor
@XmlRootElement(name="newConcept")
@XmlType(
    name = "newConceptType",
    propOrder = {
    "name",
    "scopeNotes"
})
@XmlAccessorType(XmlAccessType.NONE)
@JsonTypeName("concept")
public final class GTAANewGenericConcept extends AbstractGTAANewConcept  {

    @Getter
    @Setter
    @XmlElement
    private String name;

    @Getter
    @Setter
    @XmlAttribute
    private Scheme objectType;

    @lombok.Builder
    public GTAANewGenericConcept(
        String name,
        @lombok.Singular List<String> scopeNotes,
        Scheme scheme) {
        this.name = name;
        this.scopeNotes = scopeNotes;
        this.objectType = scheme;
    }


}
