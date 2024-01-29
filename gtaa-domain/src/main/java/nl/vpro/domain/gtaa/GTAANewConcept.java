package nl.vpro.domain.gtaa;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import nl.vpro.openarchives.oai.Label;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "newObjectType"
)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = GTAANewPerson.class, name = "person"),
        @JsonSubTypes.Type(value = GTAANewGenericConcept.class, name = "concept")
    })
@XmlSeeAlso({
    GTAANewPerson.class,
    GTAANewGenericConcept.class
})
public sealed interface GTAANewConcept permits AbstractGTAANewConcept {
    String getName();

    List<String> getScopeNotes();

    Scheme getObjectType();

    default List<Label> getScopeNotesAsLabel() {
        List<String> scopeNotes = getScopeNotes();
        if (scopeNotes != null) {
            return scopeNotes
                .stream()
                .map(Label::forValue)
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
