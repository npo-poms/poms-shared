package nl.vpro.domain.gtaa;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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
public abstract class AbstractThesaurusItem implements ThesaurusObject, Serializable {


    @Getter
    @Setter
    @XmlAttribute
    private String id;

    @Getter
    @Setter
    List<Label> notes;

    @Getter
    @Setter
    String value;

    @Getter
    @Setter
    String redirectedFrom;

    @Getter
    @Setter
    @XmlAttribute
    Status status;

    @Getter
    @Setter
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    Instant lastModified;


    AbstractThesaurusItem() {

    }

    AbstractThesaurusItem(String id, List<Label> notes, String value, String redirectedFrom, Status status, Instant lastModified) {
        this.id = id;
        this.notes = notes;
        this.value = value;
        this.redirectedFrom = redirectedFrom;
        this.status = status;
        this.lastModified = lastModified;
    }

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

    protected void fill(String id,
                        String value,
                        List<Label> notes,
                        Status status,
                        String changeNote,
                        Instant modified) {
        this.setId(id);
        this.setValue(value);
        this.setNotes(notes);
        this.setStatus(status);
        if (changeNote != null) {
            this.setRedirectedFrom(changeNote);
        }
        if (modified != null) {
            this.setLastModified(modified);

        }


    }
}
