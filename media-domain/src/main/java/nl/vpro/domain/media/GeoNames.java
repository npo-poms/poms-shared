package nl.vpro.domain.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.validation.NoHtml;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoNamesType")
@Getter
@Setter
public class GeoNames extends DomainObject implements Child<MediaObject>, Comparable<GeoNames>, Ownable {

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
    @XmlElement(name="geoName")
    private List<GeoName> values = new ArrayList<>();

    public GeoNames() {
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoNames(@NonNull @Singular List<GeoName> values, @NonNull OwnerType owner) {
        this.values = values.stream().map(GeoName::copy).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }


    public GeoNames copy() {
        return new GeoNames(values.stream().map(GeoName::copy).collect(Collectors.toList()), owner);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoNames geoNames = (GeoNames) o;
        return owner == geoNames.owner &&
                values.equals(geoNames.values);
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
    public int compareTo(GeoNames o) {
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
        return "GeoNames:" + owner + ":" + values;
    }

}
