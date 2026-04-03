package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.io.Serial;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;


/**
 *
 * @author Giorgio Vinci
 * @since 5.11
 */
@Entity
@XmlType(name = "geoLocationsType")
@Getter
@Setter
public class GeoLocations extends AbstractMediaObjectOwnableList<GeoLocations, GeoLocation> {

    @Serial
    private static final long serialVersionUID = 4815604300070005169L;

    public static GeoLocations empty(@NonNull  OwnerType owner){
        return GeoLocations.builder().owner(owner).build();
    }

    public GeoLocations() {
    }
    public GeoLocations(@lombok.NonNull MediaObject parent, @lombok.NonNull  OwnerType owner) {
        this.parent = parent;
        this.owner = owner;
    }


    @lombok.Builder(builderClassName = "Builder")
    private GeoLocations(
        @lombok.NonNull @Singular List<GeoLocation> values,
        @lombok.NonNull OwnerType owner,
        MediaObject parent) {
        this.values = values.stream().map(GeoLocation::clone).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
        this.parent = parent;
    }

    @Override
    @NonNull
    @XmlElement(name="geoLocation")
    @JsonProperty("values")
    public List<GeoLocation> getFilteredValues() {
        return values;
    }

    public void setValues(List<GeoLocation> list) {
        this.values = list;
    }

}
