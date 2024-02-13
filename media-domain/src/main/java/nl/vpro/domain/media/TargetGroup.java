package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.Serial;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.support.SimpleValueMediaObjectOwnableListItem;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "targetGroupType")
@Getter
@Setter
@JsonSerialize(using = TargetGroup.Serializer.class)
@JsonDeserialize(using = TargetGroup.Deserializer.class)
public class TargetGroup extends DomainObject implements SimpleValueMediaObjectOwnableListItem<TargetGroup, TargetGroups, TargetGroupType> {

    @Serial
    private static final long serialVersionUID = 6530310295897331244L;

    @ManyToOne(targetEntity = TargetGroups.class, fetch = FetchType.LAZY)
    @XmlTransient
    private TargetGroups parent;

    @Enumerated(EnumType.STRING)
    @XmlValue
    public TargetGroupType value;


    public TargetGroup() {}

    @lombok.Builder(builderClassName = "Builder")
    public TargetGroup(TargetGroupType value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetGroup that = (TargetGroup) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }


    @Override
    public TargetGroup clone() {
        try {
            return (TargetGroup) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }

    }

    @Override
    public int compareTo(TargetGroup o) {
        return 0;

    }


    public static class Serializer extends JsonSerializer<TargetGroup> {
        @Override
        public void serialize(TargetGroup value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getValue().name());
        }
    }


    public static class Deserializer extends JsonDeserializer<TargetGroup> {
        @Override
        public TargetGroup deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return new TargetGroup(TargetGroupType.valueOf(p.getValueAsString()));
        }
    }
}
