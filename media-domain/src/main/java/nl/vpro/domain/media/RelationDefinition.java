package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.*;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.user.Broadcaster;

import static nl.vpro.domain.media.MediaObjectFilters.BROADCASTER_FILTER;
import static nl.vpro.domain.media.MediaObjectFilters.PARAMETER_BROADCASTERS;

@Entity
@IdClass(RelationDefinitionIdentifier.class)
@FilterDef(name = BROADCASTER_FILTER, parameters = {@ParamDef(name = PARAMETER_BROADCASTERS, type = "string")})
@Filter(name = BROADCASTER_FILTER, condition = "broadcaster in (:broadcasters)")
public class RelationDefinition implements Serializable, Identifiable<RelationDefinitionIdentifier> {

    @Serial
    private static final long serialVersionUID = -1658542635995973742L;

    public static RelationDefinition of(String type, Broadcaster broadcaster) {
        return new RelationDefinition(type, broadcaster.getId());
    }
    public static RelationDefinition of(String type, String broadcaster) {
        return new RelationDefinition(type, broadcaster);
    }

    @Id
    @NotNull(message = "{nl.vpro.constraints.NotEmpty}")
    @Pattern(regexp = "[A-Z0-9_-]{4,}", message = "{nl.vpro.constraints.relationDefinition.Pattern}")
    @Getter
    private String type;

    @Id
    @NotNull(message = "{nl.vpro.constraints.NotEmpty}")
    @Size(min = 1)
    @Getter
    @Setter
    private String broadcaster;

    @Getter
    @Setter
    private String displayText;

    protected RelationDefinition() {
    }

    public RelationDefinition(String type, String broadcaster) {
        this(type, broadcaster, null);
    }

    @lombok.Builder
    public RelationDefinition(String type, String broadcaster, String displayText) {
        setType(type);
        this.broadcaster = broadcaster;
        this.displayText = displayText;
    }


    public void setType(@NonNull String type) {
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
