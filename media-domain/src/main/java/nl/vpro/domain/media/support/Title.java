package nl.vpro.domain.media.support;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.*;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.validation.NoHtml;

/**
 * A {@link MediaObject} can have more than one title which should differ in type and
 * owner.
 * <p>
 * To some extent a titles type describes a usage scenario. In most cases the main title
 * will be used.
 * <p>
 * The title owner describes the origin of the title. Several media suppliers provide
 * their own titles. To prevent conflicts while updating incoming data, all titles are
 * stored for later usage. With the restriction that there are no titles t1 and t2 such
 * that t1.equals(t2).
 * <p>
 * This class confirms to a natural ordering consistent with equals based on its type
 * and owner. Beware of the fact that the title value itself has no part in the ordering
 * algorithm. Two titles are equal if their owner and type are equal. When adding a title
 * to a SortedSet which already contains an equal title, the added title automatically
 * replaces the existing title.
 * <p>
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
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "titleType", namespace = Xmlns.MEDIA_NAMESPACE)
@JsonPropertyOrder({"value", "owner", "type"})
public class Title extends AbstractOwnedText<Title> implements  Serializable, Child<MediaObject>, Identifiable<Long> {

    @Serial
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
    @XmlValue
    protected String value;

    @ManyToOne
    protected MediaObject parent;


    /**
     * Creates a new <code>Title</code> with a length of 256 characters.
     */
    public Title(@NonNull String title, @NonNull OwnerType owner, @NonNull TextualType type) {
        this(title, owner, type, true);
    }

    /**
     * Optional constructor to bypass cropping the title to a length of 256
     * characters which is the default.
     */
    public Title(@NonNull String title, @NonNull OwnerType owner, @NonNull TextualType type, boolean crop) {
        super(owner, type);
        this.value = strip(title);
        if (crop) {
            this.crop();
        }
    }

    public Title(Title source) {
        this(source, source.parent);
    }

    public Title(Title source, MediaObject parent) {
        this(source.get(), source.owner, source.type);
        this.parent = parent;
    }

    public static Title copy(Title source){
        return source == null ? null : copy(source, source.parent);
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
        if (value == null) {
            return;
        }
        if (start < 0) {
            start = 0;
        }

        if (value.length() < stop) {
            stop = value.length();
        }

        value = value.substring(start, stop);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String get() {
        return value;
    }
    @Override
    public void set(String s) {
        this.value = strip(s);
        crop();
    }


    protected static String strip(String s) {
        if (s == null){
            return null;
        }
        return s.replaceAll("[\f\\u0085\\u2028\\u2029  ]", " ");
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
        int result = super.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + owner.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.parent = (MediaObject) parent;
    }

    @Override
    public boolean mayContainNewLines() {
        return false;
    }
}
