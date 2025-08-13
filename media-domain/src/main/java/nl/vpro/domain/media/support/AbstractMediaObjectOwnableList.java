package nl.vpro.domain.media.support;


import lombok.*;

import java.io.Serial;
import java.util.*;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.ObjectUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.MediaObject;

import static jakarta.persistence.CascadeType.ALL;


/**
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractMediaObjectOwnableList<
    THIS extends AbstractMediaObjectOwnableList<THIS, I>,
    I extends MediaObjectOwnableListItem<I, THIS>>
    extends DomainObject
    implements MediaObjectOwnableList<THIS, I>  {

    @Serial
    private static final long serialVersionUID = 4657694328901583730L;

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    @Getter
    @Setter
    protected MediaObject parent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "owner_type") // PostgreSQL enum type
    @XmlAttribute
    @Setter(AccessLevel.PRIVATE)
    @Getter
    protected OwnerType owner;

    @OneToMany(orphanRemoval = true, cascade = {ALL})
    @JoinColumn(name = "parent_id")
    @OrderColumn(name = "list_index", nullable = true)
    protected List<I> values = new ArrayList<>();


    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }

    @Override
    public int compareTo(THIS o) {
        return ObjectUtils.compare(this.getOwner(), o.getOwner());
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        THIS list = (THIS) o;
        if (getParent() == null || list.getParent() == null) {
            // No parent, this is odd
            return owner == list.owner;
        } else {
            return owner == list.owner && Objects.equals(getParent().getMid(), list.getParent().getMid());
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + owner + ":" + values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public THIS clone() {
        try {
            THIS result = (THIS) super.clone();
            result.parent = null;
            for (I v : result.values) {
                v.setParent(result);
            }
            return result;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    @Override
    @NonNull
    public final List<I> getValues() {
        return values;
    }

    /**
     * Returns the values in this list, optionally filtered for XML/json. The case was that inherited targetgrups can be filtered using the agerating of the mediaobject.
     * @since 8.10
     */
    @JsonProperty("values")
    protected abstract List<I> getFilteredValues();

}
