package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Supplier;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.i18n.Displayable;

/**
 * A reference to a social media account of hashtag. Used to be only twitter, hence the name.
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@nl.vpro.validation.SocialRef
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "twitterRefType")
public class SocialRef implements Serializable, Supplier<String>, MutableOwnable {

    @Serial
    private static final long serialVersionUID = -9215030144570677716L;

    public enum Type implements Displayable {
        ACCOUNT("X-account"),
        HASHTAG("hash tag"),
        BSKY("Bluesky account"),
        LINKEDIN("Linkedin account"),
        FACEBOOK("Facebook account"),
        INSTAGRAM("Facebook account"),
        MASTODON("Mastodon account"),
        OTHER("Other social media account")
        ;


        @Getter
        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }
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


    public SocialRef() {
    }

    public SocialRef(String v) {
        this(v, OwnerType.BROADCASTER);
    }
    public SocialRef(String v, OwnerType owner) {
        type = detectType(v);
        value = v;
        this.owner = owner;
    }

    public SocialRef(SocialRef source) {
        this(source.value, source.owner);
    }

    public static SocialRef copy(SocialRef source) {
        if(source == null) {
            return null;
        }
        return new SocialRef(source);
    }


    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        SocialRef that = (SocialRef)o;

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

    public static String getValueOrNull(SocialRef ref) {
        return ref == null ? null : ref.getValue();
    }

    public static Type detectType(String v) {
        if (v.startsWith("@")) {
            if (v.contains("bsky.social")) {
                return Type.BSKY;
            }
            if (v.contains("mastodon")) {
                return Type.MASTODON;
            }
            if (v.contains("linkedin.com")) {
                return Type.LINKEDIN;
            }
            if (v.contains("instagram.com")) {
                return Type.INSTAGRAM;
            }
            if (v.contains("facebook.com")) {
                return Type.FACEBOOK;
            }
            return Type.ACCOUNT;
        } else if (v.startsWith("#")) {
            return Type.HASHTAG;
        }
        return Type.OTHER;
    }
}
