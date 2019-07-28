package nl.vpro.domain.media.support;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.MediaObject;

import static javax.persistence.CascadeType.ALL;


/**
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
@Getter
@Setter
public abstract class AbstractMediaObjectOwnableList<
    THIS extends AbstractMediaObjectOwnableList<THIS, I>,
    I extends MediaObjectOwnableListItem<I, THIS>>
    extends DomainObject
    implements MediaObjectOwnableList<THIS, I>  {

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    private MediaObject parent;

    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @Setter(AccessLevel.PRIVATE)
    protected OwnerType owner;

    @OneToMany(cascade = {ALL})
    @JoinColumn(name = "parent_id")
    @JsonProperty("values")
    @OrderColumn(name = "list_index", nullable = true)
    protected List<I> values = new ArrayList<>();


    @Override
    public int hashCode() {
        return Objects.hash(owner, values);
    }

    @Override
    public int compareTo(THIS o) {
        if (this.getOwner().equals(o.getOwner())){
            if (!Objects.equals(this.values, o.getValues())) {
                //order is undefined (we never expect 2 intentions with same owner in a set anyway)
                return -1;
            }
        }
        return this.getOwner().compareTo(o.getOwner());
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        THIS list = (THIS) o;
        return owner == list.owner &&
                values.equals(list.values);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + owner + ":" + values;
    }

}
