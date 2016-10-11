package nl.vpro.domain.media;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotEmpty;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.user.Broadcaster;

@Entity
@IdClass(RelationDefinitionIdentifier.class)
@FilterDefs({
    @FilterDef(name = "broadcasterFilter", parameters = {
        @ParamDef(name = "broadcasters", type = "string")})
})
@Filters({
    @Filter(name = "broadcasterFilter",
        condition = "broadcaster in (:broadcasters)")
})
public class RelationDefinition implements Serializable, Identifiable<RelationDefinitionIdentifier> {

    public static RelationDefinition of(String type, Broadcaster broadcaster) {
        return new RelationDefinition(type, broadcaster.getId());
    }
    public static RelationDefinition of(String type, String broadcaster) {
        return new RelationDefinition(type, broadcaster);
    }

    @Id
    @NotEmpty(message = "{nl.vpro.constraints.NotEmpty}")
    @Pattern(regexp = "[A-Z0-9_-]{4,}", message = "{nl.vpro.constraints.relationDefinition.Pattern}")
    private String type;

    @Id
    @NotEmpty(message = "{nl.vpro.constraints.NotEmpty}")
    private String broadcaster;

    private String displayText;

    protected RelationDefinition() {
    }

    public RelationDefinition(String type, String broadcaster) {
        this(type, broadcaster, null);
    }

    public RelationDefinition(String type, String broadcaster, String text) {
        setType(type);
        this.broadcaster = broadcaster;
        this.displayText = text;
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if(type != null) {
            type = type.toUpperCase();
        }

        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RelationDefinition that = (RelationDefinition)o;
        return
            (type == null ? that.getType() == null : type.equals(that.getType()))
                &&
            (broadcaster == null ? that.getBroadcaster() == null : broadcaster.equals(that.getBroadcaster()));
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (broadcaster != null ? broadcaster.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("type", type)
            .append("broadcaster", broadcaster)
            .append("displayText", displayText)
            .toString();
    }

    @Override
    public RelationDefinitionIdentifier getId() {
        return new RelationDefinitionIdentifier(type, broadcaster);
    }
}
