package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.function.Supplier;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.validation.PomsValidatorGroup;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@nl.vpro.validation.TwitterRef
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "twitterRefType")
public class TwitterRef implements Serializable, Supplier<String>, MutableOwnable {

    public enum Type {
        ACCOUNT,
        HASHTAG
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @Getter
    @Setter
    private Type type;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @Pattern(
        message = "{nl.vpro.constraints.twitterRefs.Pattern}",
        regexp="^[@#][A-Za-z0-9_]{1,139}$",
        groups={PomsValidatorGroup.class}
    )
    @XmlValue
    @JsonProperty("value")
    @Getter
    @Setter
    private String value;

    @XmlTransient
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OwnerType owner;


    public TwitterRef() {
    }

    public TwitterRef(String v) {
        this(v, OwnerType.BROADCASTER);
    }
    public TwitterRef(String v, OwnerType owner) {
        if(v.startsWith("@")) {
            type = Type.ACCOUNT;
        } else if(v.startsWith("#")) {
            type = Type.HASHTAG;
        }
        value = v;
        this.owner = owner;
    }

    public TwitterRef(TwitterRef source) {
        this(source.value, source.owner);
    }

    public static TwitterRef copy(TwitterRef source) {
        if(source == null) {
            return null;
        }
        return new TwitterRef(source);
    }


    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        TwitterRef that = (TwitterRef)o;

        return type == that.type && value.equals(that.value);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return value;
    }


    @Override
    public String get() {
        return value;

    }

    public static String getValueOrNull(TwitterRef ref) {
        return ref == null ? null : ref.getValue();
    }
}
