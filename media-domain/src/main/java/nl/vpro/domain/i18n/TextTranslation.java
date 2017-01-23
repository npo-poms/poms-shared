package nl.vpro.domain.i18n;

import lombok.ToString;

import java.util.function.Supplier;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import nl.vpro.validation.NoHtml;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@MappedSuperclass
@ToString
public class TextTranslation<P>  implements Supplier<String> {
    @Column(nullable = false)
    @NoHtml
    protected String value;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @ManyToOne
    private P parent;

    public TextTranslation(String value) {
        this.value = value;
    }

    protected TextTranslation() {
    }


    @Override
    public String get() {
        return value;
    }

    public void set(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public P getParent() {
        return parent;
    }

    public void setParent(P parent) {
        this.parent = parent;
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
        if (object == null) {
            return false;
        }

        if (this.getClass() != object.getClass()) {
            return false;
        }

        TextTranslation<P> that = (TextTranslation<P>) object;

        if (this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        return this.value == null ? that.value == null : this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }

        if (value != null) {
            return value.hashCode();
        }

        return System.identityHashCode(this);
    }

}
