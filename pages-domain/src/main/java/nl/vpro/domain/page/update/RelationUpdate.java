package nl.vpro.domain.page.update;

import java.io.Serial;
import java.io.Serializable;

import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.page.*;
import nl.vpro.domain.page.validation.ValidRelation;
import nl.vpro.domain.user.Broadcaster;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relationUpdateType",
    propOrder = {
        "text"
    })
@ValidRelation
public class RelationUpdate implements Comparable<RelationUpdate>, Serializable {

    @Serial
    private static final long serialVersionUID = 5444795749320211891L;

    public static RelationUpdate of(Relation relation) {
        return RelationUpdate.builder()
            .type(relation.getType())
            .broadcaster(relation.getBroadcaster())
            .uriRef(relation.getUriRef())
            .text(relation.getText())
            .build();
    }

    @XmlAttribute(required = true)
    private String type;

    @XmlAttribute(required = true)
    private String broadcaster;

    @XmlAttribute
    private String uriRef;

    @XmlValue
    private String text;


    public static RelationUpdate of(RelationDefinition def, String uriRef, String text) {
        return new RelationUpdate(def, uriRef, text);
    }

    public static RelationUpdate text(RelationDefinition def, String text) {
        return new RelationUpdate(def, null, text);
    }

    public static RelationUpdate  uri(RelationDefinition def, String uri) {
        return new RelationUpdate(def, uri, null);
    }


    public RelationUpdate() {
    }

    public RelationUpdate(String type, String broadcaster) {
        this.type = type;
        this.broadcaster = broadcaster;
    }

    public RelationUpdate(String type, String broadcaster, String uriRef, String text) {
        this(type, broadcaster);
        this.uriRef = uriRef;
        this.text = text;
    }

    public RelationUpdate(RelationDefinition def, String uriRef, String text) {
        this(def.getType(), def.getBroadcaster(), uriRef, text);
    }

    @lombok.Builder
    public RelationUpdate(String type, String broadcaster, String uriRef, String text, String urn) {
        this(type, broadcaster);
        this.uriRef = uriRef;
        this.text = text;
    }

    public RelationUpdate(Relation relation) {
        this(relation.getType(), relation.getBroadcaster(), relation.getUriRef(), relation.getText());
    }

    public Relation toRelation(RelationDefinitionService relationDefinitionService) {
        RelationDefinition relationDefinition = relationDefinitionService.get(type, new Broadcaster(broadcaster));
        if (relationDefinition == null){
            throw new IllegalArgumentException("The relationtype " +  getBroadcaster() + "/" + getType() + "  is not defined");
        }
        return new Relation(
            relationDefinition,
            uriRef,
            text
        );
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUriRef() {
        return uriRef;
    }

    public void setUriRef(String uriRef) {
        this.uriRef = uriRef;
    }

    public RelationDefinition getDefinition() {
        return RelationDefinition.of(type, broadcaster);
    }

    @Override
    public int compareTo(@NonNull RelationUpdate relationUpdate) {
        if (broadcaster != null && relationUpdate.broadcaster != null && broadcaster.compareTo(relationUpdate.broadcaster) != 0) {
            return broadcaster.compareTo(relationUpdate.broadcaster);
        }

        if (type != null && relationUpdate.type != null && type.compareTo(relationUpdate.type) != 0) {
            return type.compareTo(relationUpdate.type);
        }

        if (uriRef != null && relationUpdate.uriRef != null && uriRef.compareTo(relationUpdate.uriRef) != 0) {
            return uriRef.compareTo(relationUpdate.uriRef);
        }


        if (text != null && relationUpdate.text != null && text.compareTo(relationUpdate.text) != 0) {
            return text.compareTo(relationUpdate.text);
        }

        return hashCode() - relationUpdate.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationUpdate that = (RelationUpdate) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (broadcaster != null ? !broadcaster.equals(that.broadcaster) : that.broadcaster != null) return false;
        if (uriRef != null ? !uriRef.equals(that.uriRef) : that.uriRef != null) return false;
        return text != null ? text.equals(that.text) : that.text == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (broadcaster != null ? broadcaster.hashCode() : 0);
        result = 31 * result + (uriRef != null ? uriRef.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
