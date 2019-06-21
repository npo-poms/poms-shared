package nl.vpro.domain.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoLocationsType")
@Getter
@Setter
public class GeoLocations extends DomainObject implements Child<MediaObject>, Comparable<GeoLocations>, Ownable {

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    private MediaObject parent;

    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @Setter(AccessLevel.PRIVATE)
    private OwnerType owner;

    @OneToMany(cascade = {ALL})
    @JoinColumn(name = "parent_id")
    @JsonProperty("values")
    @OrderColumn(name = "list_index", nullable = true)
    @XmlElement(name="geoLocation")
    private List<GeoLocation> values = new ArrayList<>();

    public GeoLocations() {
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoLocations(@NonNull @Singular List<GeoLocation> values, @NonNull OwnerType owner, MediaObject parent) {
        this.values = values.stream().map(GeoLocation::copy).collect(Collectors.toList());
        this.owner = owner;
        this.parent = parent;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }


    public GeoLocations copy() {
        return new GeoLocations(values.stream().map(GeoLocation::copy).collect(Collectors.toList()), owner, parent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoLocations geoLocations = (GeoLocations) o;
        return owner == geoLocations.owner &&
                values.equals(geoLocations.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, values);
    }

    /**
     *  Just ensuring the comparator match equality.
     *  We never expect 2 lists from the same owner anyway
     */
    @Override
    public int compareTo(GeoLocations o) {
        if (this.getOwner().equals(o.getOwner())){
            if (!Objects.equals(this.values, o.values)) {
                //order is undefined (we never expect 2 intentions with same owner in a set anyway)
                return -1;
            }
        }
        return this.getOwner().compareTo(o.getOwner());
    }

    @Override
    public String toString() {
        return "GeoLocations:" + owner + ":" + values;
    }

}
