package nl.vpro.domain.media.support;

import lombok.ToString;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.AbstractOwnedText;
import nl.vpro.domain.Child;
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
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "descriptionType",
    namespace = Xmlns.MEDIA_NAMESPACE)
@JsonPropertyOrder({"value", "owner", "type"})

@ToString(exclude = "parent")
public class Description extends AbstractOwnedText<Description> implements Serializable, Child<MediaObject> {
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
    @JsonProperty("value")

    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 64000, message = "{nl.vpro.constraints.text.Size.max}")
    })
    protected String description;

    @XmlTransient
    @ManyToOne(targetEntity = MediaObject.class)
    protected MediaObject parent;

    public Description() {
    }

    public Description(String description, OwnerType owner, TextualType type) {
        super(owner, type);
        this.description = description;
    }

    public Description(Description source) {
        this(source, source.parent);
    }


    public <S extends MediaObject> Description(Description source, MediaObject parent) {
        this(source.get(), source.getOwner(), source.getType());
        this.parent = parent;
    }

    public static Description copy(Description source){
        return source == null ? null : copy(source, source.parent);
    }

    public static Description copy(Description source, MediaObject parent){
        return source == null ? null : new Description(source, parent);
    }

    public static Description main(String main, OwnerType type) {
        return new Description(main, type, TextualType.MAIN);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = strip(description);
    }

    @Override
    public String get() {
        return getDescription();
    }
    @Override
    public void set(String s) {
        setDescription(s);
    }

    protected static String strip(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[\f\\u0085\\u2028\\u2029  ]", "\n");
    }

    @Override
    @XmlTransient
    public MediaObject getParent() {
        return parent;
    }

    @Override
    public void setParent(MediaObject parent) {
        this.parent = parent;
    }

    protected Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
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


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.parent = (MediaObject) parent;
    }
}
