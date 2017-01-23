package nl.vpro.domain.media;

import java.io.Serializable;
import java.util.function.Supplier;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@nl.vpro.validation.TwitterRef
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "twitterRefType")
public class TwitterRef implements Serializable, Supplier<String> {

    public enum Type {
        ACCOUNT, HASHTAG
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    private Type type;

    @Column(nullable = false)
    // Validating here trigged an ValidatorImpl isReachable exception
    //    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    //    @Pattern(message = "{nl.vpro.constraints.twitterRefs.Pattern}", regexp="^(@|#)[A-Za-z0-9_]{1,15}$")
    @XmlValue
    @JsonProperty("value")
    private String value;

    public TwitterRef() {
    }

    public TwitterRef(String v) {
        if(v.startsWith("@")) {
            type = Type.ACCOUNT;
        } else if(v.startsWith("#")) {
            type = Type.HASHTAG;
        }
        value = v;
    }

    public TwitterRef(TwitterRef source) {
        this(source.value);
    }

    public static TwitterRef copy(TwitterRef source) {
        if(source == null) {
            return null;
        }
        return new TwitterRef(source);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
