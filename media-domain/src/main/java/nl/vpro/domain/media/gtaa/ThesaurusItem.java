package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.w3.rdf.Description;

import java.time.Instant;

import nl.vpro.xml.bind.InstantXmlAdapter;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ThesaurusItem implements ThesaurusObject {


    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private List<Label> notes;
    @Getter
    @Setter
    String value;
    @Getter
    @Setter
    String type;

    @Getter
    @Setter
    String redirectedFrom;

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant lastModified;


    public static ThesaurusItem create(Description description) {
        final ThesaurusItem answer = new ThesaurusItem();
        answer.setId(description.getAbout());
        answer.setValue(description.getPrefLabel().getValue());
        answer.setNotes(description.getScopeNote());
        answer.setStatus(description.getStatus());
        answer.setType(description.getType().getResource());
        if (description.getChangeNote() != null) {
            answer.setRedirectedFrom(description.getRedirectedFrom());
        }
        if (description.getModified() != null) {
            answer.setLastModified(description.getModified().getValue().toInstant());
        }
        return answer;
        }


}
