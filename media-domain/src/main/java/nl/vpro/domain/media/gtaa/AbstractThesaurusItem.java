package nl.vpro.domain.media.gtaa;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractThesaurusItem implements ThesaurusObject {


    @Getter
    @Setter
    @XmlAttribute
    private String id;

    @Getter
    @Setter
    private List<Label> notes;

    @Getter
    @Setter
    String value;

    @Getter
    @Setter
    String redirectedFrom;

    @Getter
    @Setter
    @XmlAttribute
    private Status status;

    @Getter
    @Setter
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant lastModified;


    protected static void fill(Description description, AbstractThesaurusItem answer) {
        answer.setId(description.getAbout());
        answer.setValue(description.getPrefLabel().getValue());
        answer.setNotes(description.getScopeNote());
        answer.setStatus(description.getStatus());
        if (description.getChangeNote() != null) {
            answer.setRedirectedFrom(description.getRedirectedFrom());
        }
        if (description.getModified() != null) {
            answer.setLastModified(description.getModified().getValue().toInstant());
        }
    }


}
