package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import javax.xml.XMLConstants;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;

/**
 * Wrapper for email, also keeping track of the owner (which currently is not yet exposed in API's).
 *
 * @since 7.7
 */

@Entity
@Table(name = "mediaobject_email")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = XMLConstants.W3C_XML_SCHEMA_NS_URI, name = "string")
@JsonFormat(shape = JsonFormat.Shape.STRING)
@Valid
public class Email implements Serializable, Supplier<String>, MutableOwnable, Updatable<Email> {

    @Serial
    private static final long serialVersionUID = -8844351970679204585L;

    @Id
    @GeneratedValue
    @XmlTransient
    private long id;

    @XmlValue
    @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}")
    @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    @Getter
    @Setter
    @jakarta.validation.constraints.Email(
        message = "{nl.vpro.constraints.Email.message}",
        groups = Default.class)
    @JsonValue
    private String email;

    @XmlTransient
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OwnerType owner;

    public Email() {
    }

    @JsonCreator
    public Email(@NonNull String email) {
        this(email, OwnerType.BROADCASTER);
    }

    public Email(@NonNull String email, @NonNull OwnerType owner) {
        this.email = email;
        this.owner = owner;
    }

    public Email(@NonNull Email source) {
        this(source.email, source.owner);
    }

    public static @PolyNull Email copy(@PolyNull Email source) {
        if(source == null) {
            return null;
        }
        return new Email(source);
    }



    @Override
    public void update(Email from) {
        email = from.getEmail();
        owner = from.owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Email email1 = (Email) o;

        if (!Objects.equals(email, email1.email)) return false;
        return owner == email1.owner;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }

    @Override
    public String get() {
        return email;
    }

    @Override
    public String toString() {
        return owner + ":"+ email;
    }

}
