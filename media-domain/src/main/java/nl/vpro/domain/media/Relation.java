package nl.vpro.domain.media;

import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.Identifiable;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;


/**
 * A relation is a free property in POMS. Every broadcaster can define their own set (see {@link RelationDefinition}
 * Besides its definition it contains two field: A free form text field, and an uri field. One or both of them can be filled.
 *
 */
@Setter
@Entity
@Cacheable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relationType", propOrder = {
        "text"
        })
@JsonPropertyOrder({
    "uriRef",
    "value",
    "type",
    "urn",
    "broadcaster"
})
public class Relation implements Comparable<Relation>, Serializable, Identifiable<Long> {

    private static final String BASE_URN = "urn:vpro:media:relation:";

    private static final Pattern URN_PATTERN = Pattern.compile("(" + BASE_URN + ")(\\d+)");

    @Serial
    private static final long serialVersionUID = -2940513328653799208L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id; // Can not extend from DomainObject because of an XmlValue annotation...

    @ManyToOne(optional = false)
    @JoinColumns({
       @JoinColumn(name = "definition_broadcaster", referencedColumnName = "broadcaster"),
       @JoinColumn(name = "definition_type", referencedColumnName = "type")

    })
    @Valid
    @NotNull
    @XmlTransient
    private RelationDefinition definition;

    @XmlAttribute
    @Size(max = 255)
    @URI
    private String uriRef;

    @XmlValue
    @Size(max = 255)
    @NoHtml(aggressive = false)
    private String text;


    public static Relation of(RelationDefinition def, String uriRef, String text) {
        return new Relation(def, uriRef, text);
    }

    public static Relation ofText(RelationDefinition def, String text) {
        return new Relation(def, null, text);
    }

    public static Relation ofUri(RelationDefinition def, String uri) {
        return new Relation(def, uri, null);
    }


    public Relation() {
    }

    public Relation(RelationDefinition definition) {
        this.definition = definition;
    }

    public Relation(RelationDefinition definition, String uriRef, String text) {
        this.definition = definition;
        this.uriRef = uriRef;
        this.text = text;
    }


    @lombok.Builder
    public Relation(Long id, RelationDefinition definition, String uriRef, String text) {
        this.id = id;
        this.definition = definition;
        this.uriRef = uriRef;
        this.text = text;
    }

    public Relation(Relation source) {
        this(source.definition, source.uriRef, source.text);
    }

    public static Relation copy(Relation source) {
        if(source == null) {
            return null;
        }
        return new Relation(source);
    }

    public static Long idFromUrn(String urn) {
        final String id = urn.substring(BASE_URN.length());
        return Long.valueOf(id);
    }

    public static Relation update(Relation from, Relation to) {
        if(from != null) {
            if(to == null) {
                to = new Relation(from.getDefinition());
            }

            if(from.getDefinition() == null || !from.getDefinition().equals(to.getDefinition())) {
                throw new UnsupportedOperationException(String.format("Can not update the relation definition for %1$s to %2$s", to, from.getDefinition()));
            }

            to.setType(from.getType());
            to.setUriRef(from.getUriRef());
            to.setText(from.getText());

        } else {
            to = null;
        }

        return to;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public RelationDefinition getDefinition() {
        return definition;
    }

    public String getUriRef() {
        return uriRef;
    }

    @XmlAttribute(required = true)
    public String getType() {
        return definition.getType();
    }

    public void setType(String type) {
        if(definition == null) {
            definition = new RelationDefinition();
        }

        definition.setType(type);
    }

    @XmlAttribute(required = true)
    public String getBroadcaster() {
        return definition.getBroadcaster();
    }

    public void setBroadcaster(String broadcaster) {
        if(definition == null) {
            definition = new RelationDefinition();
        }

        definition.setBroadcaster(broadcaster);
    }

    @XmlAttribute
    public String getUrn() {
        return (id != null) ? BASE_URN + id : null;
    }

    public void setUrn(String urn) {
        if (urn == null) {
            this.id = null;
            return;
        }
        Matcher matcher = URN_PATTERN.matcher(urn);
        if(!matcher.find() || matcher.groupCount() != 2) {
            throw new IllegalArgumentException("Unexpected urn format: " + urn);
        }

        this.id = Long.valueOf(matcher.group(2));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Relation relation = (Relation)o;

        if (id != null && relation.getId() != null) {
            return id.equals(relation.getId());
        } else {
            return Objects.equals(definition, relation.definition) &&
                Objects.equals(uriRef, relation.uriRef) &&
                Objects.equals(text, relation.text);

        }

    }

    @Override
    public int hashCode() {
        int result  = (definition != null ? definition.hashCode() : 0);
        result = 31 * result + (uriRef != null ? uriRef.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(@NonNull Relation r) {
        if(definition != null) {
            if(definition.getBroadcaster().compareTo(r.getBroadcaster()) != 0) {
                return definition.getBroadcaster().compareTo(r.getBroadcaster());
            } else if(definition.getType().compareTo(r.getType()) != 0) {
                return definition.getType().compareTo(r.getType());
            }
        }

        if(uriRef != null && r.uriRef != null && uriRef.compareTo(r.uriRef) != 0) {
            return uriRef.compareTo(r.uriRef);
        }

        if(text != null && r.text != null && text.compareTo(r.text) != 0) {
            return text.compareTo(r.text);
        }

        if(id != null && r.id != null) {
            return id.compareTo(r.id);
        }

        return 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("definition", definition)
            .append("uriRef", uriRef)
            .append("text", text)
            .toString();
    }
}
