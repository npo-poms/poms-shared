package nl.vpro.domain.gtaa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
public abstract class AbstractThesaurusItem implements ThesaurusObject, Serializable {


    @Getter
    @Setter
    @XmlAttribute
    private URI id;

    @Getter
    @Setter
    @XmlElement
    List<Label> notes;

    @Getter
    @Setter
    String value;

    @Getter
    @Setter
    @XmlElement
    URI redirectedFrom;

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

    AbstractThesaurusItem(URI id, List<Label> notes, String value, URI redirectedFrom, Status status, Instant lastModified) {
        this.id = id;
        this.notes = notes;
        this.value = value;
        this.redirectedFrom = redirectedFrom;
        this.status = status;
        this.lastModified = lastModified;
    }

    protected static void fill(Description description, AbstractThesaurusItem answer) {
        answer.setId(description.getAbout());
        if (description.getPrefLabel() != null) {
            answer.setValue(description.getPrefLabel().getValue());
        }
        answer.setNotes(description.getScopeNote());
        answer.setStatus(description.getStatus());
        if (description.getChangeNote() != null) {
            description.getRedirectedFrom().ifPresent(answer::setRedirectedFrom);
        }
        if (description.getModified() != null) {
            answer.setLastModified(description.getModified().getValue().toInstant());
        }
    }

    protected void fill(URI id,
                        String value,
                        List<Label> notes,
                        Status status,
                        URI changeNote,
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
