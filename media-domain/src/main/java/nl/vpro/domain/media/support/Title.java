package nl.vpro.domain.media.support;

import lombok.ToString;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.validation.NoHtml;

/**
 * A {@link MediaObject} can have more than one title which should differ in type and
 * owner.
 * <p/>
 * To some extend a titles type describes a usage scenario. In most cases the main title
 * will be used.
 * <p/>
 * The title owner describes the origin of the title. Several media suppliers provide
 * there own titles. To prevent conflicts while updating incoming data, all titles are
 * stored for later usage. With the restriction that there are no titles t1 and t2 such
 * that t1.equals(t2).
 * <p/>
 * This class confirms to a natural ordering consistent with equals based on its type
 * and owner. Beware of the fact that the title value itself has no part in the ordering
 * algorithm. Two titles are equal if there owner and type are equal. When adding a title
 * to a SortedSet which already contains an equal title, the added title automatically
 * replaces the existing title.
 * <p/>
 * The ordering of titles is dictated by the order (ordinal numbers) of the enum values
 * for a titles type and ownertype.
 *
 * @author Roelof Jan Koekoek
 * @see TextualType
 * @see OwnerType
 * @since 0.4
 */
@Entity
@Cacheable
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "titleType", namespace = Xmlns.MEDIA_NAMESPACE,
    propOrder = {
            "title"
    })
@ToString(exclude = "parent")
public class Title implements Ownable, Typable<TextualType>, Comparable<Title>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NoHtml
    @JsonProperty("value")
    protected String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected OwnerType owner = OwnerType.BROADCASTER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    protected TextualType type;

    @ManyToOne
    protected MediaObject parent;

    /**
     * Creates a new <code>Title</code> with a length of 256 characters.
     */
    public Title(String title, OwnerType owner, TextualType type) {
        this(title, owner, type, true);
    }

    /**
     * Optional constructor to bypass cropping the title to a length of 256
     * characters which is the default.
     */
    public Title(String title, OwnerType owner, TextualType type, boolean crop) {
        this.title = title;
        this.owner = owner;
        this.type = type;

        if (crop) {
            this.crop();
        }
    }

    public Title(Title source) {
        this(source, source.parent);
    }

    public Title(Title source, MediaObject parent) {
        this(source.title, source.owner, source.type);
        this.parent = parent;
    }

    public static Title copy(Title source){
        return copy(source, source.parent);
    }

    public static Title copy(Title source, MediaObject parent){
        if(source == null) {
            return null;
        }

        return new Title(source, parent);
    }

    public static Title main(String main, OwnerType type) {
        return new Title(main, type, TextualType.MAIN);
    }

    public static Title main(String main) {
        return main(main, OwnerType.BROADCASTER);
    }

    public static Title sub(String main, OwnerType type) {
        return new Title(main, type, TextualType.SUB);
    }

    public static Title shortTitle(String main, OwnerType type) {
        return new Title(main, type, TextualType.SHORT);
    }

    public static Title shortTitle(String main) {
        return shortTitle(main, OwnerType.BROADCASTER);
    }

    public Title() {
    }

    public void crop() {
        crop(0, 255);
    }

    public void crop(int stop) {
        crop(0, stop);
    }

    public void crop(int start, int stop) {
        if (title == null) {
            return;
        }
        if (start < 0) {
            start = 0;
        }

        if (title.length() < stop) {
            stop = title.length();
        }

        title = title.substring(start, stop);
    }

    public Long getId() {
        return id;
    }

    @XmlValue
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        crop();
    }

    @XmlAttribute
    @Override
    public OwnerType getOwner() {
        return owner;
    }

    @Override
    public void setOwner(OwnerType value) {
        this.owner = value;
    }

    @Override
    @XmlAttribute
    public TextualType getType() {
        return type;
    }

    @Override
    public void setType(TextualType value) {
        this.type = value;
    }

    @XmlTransient
    public MediaObject getParent() {
        return parent;
    }

    public void setParent(MediaObject parent) {
        this.parent = parent;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Title tit = (Title) o;

        if (parent == null) {
            return owner == tit.getOwner() && type == tit.getType() && tit.getParent() == null;
        }

        return owner == tit.getOwner() && type == tit.getType() && parent.equals(tit.getParent());
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Title o) {
        if (o == null) {
            return -1;
        }
        if (type != null && type.equals(o.getType()) && owner != null && o.getOwner() != null) {
            return owner.ordinal() - o.getOwner().ordinal();
        }
        return (type == null ? -1 : type.ordinal()) - (o.getType() == null ? -1 : o.getType().ordinal());
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.parent = (MediaObject) parent;
    }
}
