package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Supplier;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.validation.URI;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "websiteType")
@Valid
public class Website implements UpdatableIdentifiable<Long, Website>, Serializable, Supplier<String>, MutableOwnable {

    @Serial
    private static final long serialVersionUID = 6568968749798696389L;

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;


    @XmlValue
    @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}")
    @Getter
    @Setter
    @URI(
        mustHaveScheme = true,
        minHostParts = 2,
        message = "{nl.vpro.constraints.URI}",
        groups = {nl.vpro.validation.PomsValidatorGroup.class}
    )
    private String url;

    @XmlTransient
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OwnerType owner;

    public Website() {
    }

    public Website(String url) {
        this.url = url;
    }

    public Website(String url, OwnerType owner) {
        this.url = url;
        this.owner = owner;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Website(Website source) {
        this(source.getUrl(), source.owner);
    }

    public static Website copy(Website source) {
        if(source == null) {
            return null;
        }
        return new Website(source);
    }


    /**
     * Under normal operation this should not be used!
     * <p/>
     * While testing, it sometimes comes in handy to be able to set an Id to simulate
     * a persisted object.
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void update(Website from) {
        url = from.getUrl();
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

        Website that = (Website)object;

        if(this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        return this.url == null ? that.url == null : this.url.equals(that.url);
    }

    @Override
    public int hashCode() {
        if(id != null) {
            return id.hashCode();
        }

        if(url != null) {
            return url.hashCode();
        }

        return System.identityHashCode(this);
    }

    /*
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
*/
    @Override
    public String get() {
        return url;
    }

    @Override
    public String toString() {
        return owner + ":"+ url;
    }

}
