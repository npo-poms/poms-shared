package nl.vpro.domain.gtaa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.w3.rdf.Description;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
@ToString
public abstract class AbstractGTAAConcept implements GTAAConcept, Serializable {


    @Getter
    @Setter
    @XmlAttribute
    private URI id;

    @Getter
    @Setter
    @XmlElement(name="scopeNote")
    @JsonProperty("scopeNotes")
    List<String> scopeNotes;

    @Getter
    @Setter
    String name;

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


    AbstractGTAAConcept() {

    }

    AbstractGTAAConcept(URI id, List<String> scopeNotes, String name, URI redirectedFrom, Status status, Instant lastModified) {
        this.id = id;
        this.scopeNotes = scopeNotes == null || scopeNotes.isEmpty() ? null : scopeNotes;
        this.name = name;
        this.redirectedFrom = redirectedFrom;
        this.status = status;
        this.lastModified = lastModified;
    }

    protected static void fill(Description description, AbstractGTAAConcept answer) {
        answer.setId(description.getAbout());
        if (description.getPrefLabel() != null) {
            answer.setName(description.getPrefLabel().getValue());
        }
        answer.setScopeNotes(description.getScopeNote() == null ? null : description.getScopeNote().stream().map(Label::getValue).collect(Collectors.toList()));
        answer.setStatus(description.getStatus());
        if (description.getChangeNote() != null) {
            description.getRedirectedFrom().ifPresent(answer::setRedirectedFrom);
        }
        if (description.getModified() != null) {
            answer.setLastModified(description.getModified().getValue().toInstant());
        }
    }

    protected void fill(URI id,
                        String name,
                        List<String> notes,
                        Status status,
                        URI changeNote,
                        Instant modified) {
        this.setId(id);
        this.setName(name);
        this.setScopeNotes(notes);
        this.setStatus(status);
        if (changeNote != null) {
            this.setRedirectedFrom(changeNote);
        }
        if (modified != null) {
            this.setLastModified(modified);

        }


    }
}
