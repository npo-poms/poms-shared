package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.support.SimpleValueMediaObjectOwnableListItem;

/**
 * @author Giorgio Vinci
 * @since 5.11
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "intentionType")
@Getter
@Setter
@JsonSerialize(using = Intention.Serializer.class)
@JsonDeserialize(using = Intention.Deserializer.class)
public class Intention extends DomainObject implements SimpleValueMediaObjectOwnableListItem<Intention, Intentions, IntentionType> {


    @ManyToOne(targetEntity = Intentions.class, fetch = FetchType.LAZY)
    @XmlTransient
    private Intentions parent;

    @Enumerated(EnumType.STRING)
    @XmlValue
    public IntentionType value;


    public Intention() {}

    @lombok.Builder(builderClassName = "Builder")
    public Intention(IntentionType value) {
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
        Intention intention = (Intention) o;
        return value == intention.value && Objects.equals(parent, intention.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, parent);
    }

    @Override
    public Intention clone() {
        try {
            return (Intention) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException();
        }

    }

    @Override
    public int compareTo(Intention o) {
        return value.compareTo(o.value);
    }

    public static class Serializer extends JsonSerializer<Intention> {
        @Override
        public void serialize(Intention value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getValue().name());
        }
    }


    public static class Deserializer extends JsonDeserializer<Intention> {
        @Override
        public Intention deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return new Intention(IntentionType.valueOf(p.getValueAsString()));
        }
    }
}
