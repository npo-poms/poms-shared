package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Supplier;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.validation.PomsValidatorGroup;

/**
 * @since 7.7
 */

@Entity
@Table(name = "mediaobject_email")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "emailType")
public class Email implements Serializable, Supplier<String>, MutableOwnable, Updatable<Email> {

    @Serial
    private static final long serialVersionUID = -8844351970679204585L;

    @Id
    @GeneratedValue
    @XmlTransient
    private long id;

    @XmlValue
    @Getter
    @Setter
    @javax.validation.constraints.Email(
        message = "{nl.vpro.constraints.Email.message}",
        groups = PomsValidatorGroup.class)
    private String email;

    @XmlTransient
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OwnerType owner;

    public Email() {
    }

    public Email(String email) {
        this(email, OwnerType.BROADCASTER);
    }

    public Email(String email, OwnerType owner) {
        this.email = email;
        this.owner = owner;
    }

    public Email(Email source) {
        this(source.getEmail(), source.owner);
    }

    public static Email copy(Email source) {
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

    /**
     * Checks for database identity or object identity if one side of the comparison can
     * not supply a database identity. It is advised to override this method with a more
     * accurate test which should not rely on database identity. You can rely on this
     * criterion when equality can not be deducted programmatic and a real and final
     * check is in need of human interaction. In essence this check then states that two
     * objects are supposed to be different if they can't supply the same database Id.
     *
     * @param object the object to compare with
     * @return true if both objects are equal
     */
    @Override
    public boolean equals(Object object) {
        if(object == null) {
            return false;
        }

        if(this.getClass() != object.getClass()) {
            return false;
        }

        Email that = (Email)object;


        return this.email == null ? that.email == null : this.email.equals(that.email);
    }

    @Override
    public int hashCode() {


        if(email != null) {
            return email.hashCode();
        }

        return System.identityHashCode(this);
    }


    @Override
    public String get() {
        return email;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("mail", email)
            .toString();
    }

}
