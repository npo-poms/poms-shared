package nl.vpro.domain.gtaa;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlSeeAlso;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import nl.vpro.openarchives.oai.Label;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "objectType"
)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = GTAANewPerson.class, name = "person"),
        @JsonSubTypes.Type(value = GTAANewThesaurusObject.class, name = "object")
    })
@XmlSeeAlso({
    GTAANewPerson.class,
    GTAANewThesaurusObject.class
})
public interface NewThesaurusObject<T extends ThesaurusObject> {
    String getValue();

    List<String> getNotes();

    default List<Label> getNotesAsLabel() {
        return getNotes().stream().map(Label::forValue).collect(Collectors.toList());
    }
}
