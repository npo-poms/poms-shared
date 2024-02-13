package nl.vpro.domain.page;

import lombok.EqualsAndHashCode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relationType", propOrder = {
    "text"
})
@EqualsAndHashCode
@JsonPropertyOrder({
    "value",
    "type",
    "urn",
    "broadcaster"
})
public class Relation implements Comparable<Relation> {

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
    @NoHtml
    private String text;

    public Relation() {
    }

    public static Relation of(RelationDefinition def, String uriRef, String text) {
        return new Relation(def, uriRef, text);
    }

    public static Relation text(RelationDefinition def, String text) {
        return new Relation(def, null, text);
    }


    public static Relation uri(RelationDefinition def, String uri) {
        return new Relation(def, uri, null);
    }

    public Relation(RelationDefinition definition) {
        this.definition = definition;
    }

    public Relation(RelationDefinition definition, String uriRef, String text) {
        this.definition = definition;
        this.uriRef = uriRef;
        this.text = text;
    }

    public Relation(Relation source) {
        this(source.definition, source.uriRef, source.text);
    }

    public static Relation copy(Relation source) {
        if (source == null) {
            return null;
        }
        return new Relation(source);
    }

    public static Relation update(Relation from, Relation to) {
        if (from != null) {
            if (to == null) {
                to = new Relation(from.getDefinition());
            }

            if (from.getDefinition() == null || !from.getDefinition().equals(to.getDefinition())) {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RelationDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(RelationDefinition definition) {
        this.definition = definition;
    }

    public String getUriRef() {
        return uriRef;
    }

    public void setUriRef(String uriRef) {
        this.uriRef = uriRef;
    }

    @XmlAttribute(required = true)
    public String getType() {
        return definition.getType();
    }

    public void setType(String type) {
        if (definition == null) {
            definition = new RelationDefinition();
        }

        definition.setType(type);
    }

    @XmlAttribute(required = true)
    public String getBroadcaster() {
        return definition.getBroadcaster();
    }

    public void setBroadcaster(String broadcaster) {
        if (definition == null) {
            definition = new RelationDefinition();
        }

        definition.setBroadcaster(broadcaster);
    }

    @Override
    public int compareTo(@NonNull Relation r) {
        if (definition != null) {
            if (definition.getBroadcaster().compareTo(r.getBroadcaster()) != 0) {
                return definition.getBroadcaster().compareTo(r.getBroadcaster());
            } else if (definition.getType().compareTo(r.getType()) != 0) {
                return definition.getType().compareTo(r.getType());
            }
        }

        if (uriRef != null && r.uriRef != null && uriRef.compareTo(r.uriRef) != 0) {
            return uriRef.compareTo(r.uriRef);
        }

        if (text != null && r.text != null && text.compareTo(r.text) != 0) {
            return text.compareTo(r.text);
        }

        return hashCode() - r.hashCode();

    }
}
