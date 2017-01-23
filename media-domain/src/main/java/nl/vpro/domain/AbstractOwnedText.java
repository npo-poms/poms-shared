package nl.vpro.domain;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.validation.NoHtml;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@MappedSuperclass
@XmlTransient
public abstract class AbstractOwnedText<T extends AbstractOwnedText> implements  OwnedText, Comparable<T> {

    @NoHtml
    @JsonProperty("value")
    protected String value;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected OwnerType owner = OwnerType.BROADCASTER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    protected TextualType type;


    public AbstractOwnedText(String title, OwnerType owner, TextualType type) {
        this.value = title;
        this.owner = owner;
        this.type = type;
    }

    protected AbstractOwnedText() {
    }

    @Override
    @XmlAttribute
    public TextualType getType() {
        return type;

    }

    @Override
    public void setType(TextualType type) {
        this.type = type;
    }

    @Override
    @XmlAttribute
    public OwnerType getOwner() {
        return owner;

    }

    @Override
    public void setOwner(OwnerType owner) {
        this.owner = owner;

    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String s) {
        this.value = s;

    }



    @Override
    public int hashCode() {
        int result = value != null ? value .hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(T o) {
        if (o == null) {
            return -1;
        }
        if (type != null && type.equals(o.getType()) && owner != null && o.getOwner() != null) {
            return owner.ordinal() - o.getOwner().ordinal();
        }
        return (type == null ? -1 : type.ordinal()) - (o.getType() == null ? -1 : o.getType().ordinal());
    }




}
