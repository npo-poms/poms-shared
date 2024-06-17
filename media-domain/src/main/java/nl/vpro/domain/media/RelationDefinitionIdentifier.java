package nl.vpro.domain.media;

import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RelationDefinitionIdentifier implements Serializable, Comparable<RelationDefinitionIdentifier> {

    @Serial
    private static final long serialVersionUID = -2249857834449241817L;

    @NotNull(message = "{nl.vpro.constraints.NotEmpty}")
    @Pattern(regexp = "[A-Z0-9_-]{4,}", message = "{nl.vpro.constraints.relationDefinition.Pattern}")
    private String type;

    @Setter
    @NotNull(message = "{nl.vpro.constraints.NotEmpty}")
    private String broadcaster;

    protected RelationDefinitionIdentifier() {
    }

    public RelationDefinitionIdentifier(String type, String broadcaster) {
        this.type = type.toUpperCase();
        this.broadcaster = broadcaster;
    }

    public @NonNull String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toUpperCase();
    }

    public @NonNull String getBroadcaster() {
        return broadcaster;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RelationDefinitionIdentifier other = (RelationDefinitionIdentifier) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if ((this.broadcaster == null) ? (other.broadcaster != null) : !this.broadcaster.equals(other.broadcaster)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 67 * hash + (this.broadcaster != null ? this.broadcaster.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "RelationDefinitionType " + broadcaster + "/" + type;
    }

    @Override
    public int compareTo(RelationDefinitionIdentifier o) {
        return Comparator.comparing(RelationDefinitionIdentifier::getBroadcaster, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(RelationDefinitionIdentifier::getType, Comparator.nullsLast(Comparator.naturalOrder()))
            .compare(this, o);
    }
}
