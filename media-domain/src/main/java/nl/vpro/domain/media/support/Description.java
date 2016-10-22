package nl.vpro.domain.media.support;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.validation.NoHtml;

/**
 * A {@link MediaObject} can have more than one description which should differ in type and
 * owner. See {@link Title} for further explanation of this class behaviour.
 *
 * @author Roelof Jan Koekoek
 * @see Title
 * @since 0.4
 */
@Entity
@nl.vpro.validation.Description
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "descriptionType", namespace = Xmlns.MEDIA_NAMESPACE,
         propOrder = {"description"})
public class Description implements Ownable, Typable<TextualType>, Comparable<Description>, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @Column(name = "description", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.StringType")
    @NotNull(message = "description not set")
    @NoHtml
    @XmlValue
    @Size(min = 1)
    protected String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @XmlAttribute(required = true)
    protected OwnerType owner = OwnerType.BROADCASTER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "description type not given")
    @XmlAttribute
    protected TextualType type;

    @ManyToOne(targetEntity = MediaObject.class)
    @XmlTransient
    protected MediaObject parent;

    public Description() {
    }

    public Description(String description, OwnerType owner, TextualType type) {
        this.description = strip(description);
        this.owner = owner;
        this.type = type;
    }

    public Description(Description source) {
        this(source, source.parent);
    }

    public <S extends MediaObject> Description(Description source, MediaObject parent) {
        this(source.getDescription(), source.getOwner(), source.getType());
        this.parent = parent;
    }

    public static Description copy(Description source){
        return copy(source, source.parent);
    }

    public static Description copy(Description source, MediaObject parent){
        if(source == null) {
            return null;
        }

        return new Description(source, parent);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = strip(description);
    }

    protected static String strip(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[\f\\u0085\\u2028\\u2029  ]", "\n");
    }

    @Override
    public OwnerType getOwner() {
        return owner;
    }

    @Override
    public void setOwner(OwnerType value) {
        this.owner = value;
    }

    @Override
    public TextualType getType() {
        return type;
    }

    @Override
    public void setType(TextualType value) {
        this.type = value;
    }

    public MediaObject getParent() {
        return parent;
    }

    public void setParent(MediaObject parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Description desc = (Description)o;

        if (parent == null && desc.getParent() == null) {
            return owner == desc.getOwner() && type == desc.getType();
        }

        return owner == desc.getOwner() && type == desc.getType() && parent.equals(desc.getParent());

    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Description o) {
        if(type.equals(o.getType()) && owner != null && o.getOwner() != null) {
            return owner.ordinal() - o.getOwner().ordinal();
        }
        return type.ordinal() - o.getType().ordinal();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("description", description)
            .append("owner", owner)
            .append("type", type)
            .toString();
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.parent = (MediaObject) parent;
    }
}
