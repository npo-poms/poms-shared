package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nl.vpro.domain.media.support.AbstractMediaObjectOwnableList;
import nl.vpro.domain.media.support.OwnerType;


/**
 *
 * @author Giorgio Vinci
 * @since 5.11
 */
@XmlType(name = "geoLocationsType")
@Getter
@Setter
public class GeoLocations extends AbstractMediaObjectOwnableList<GeoLocations, GeoLocation> {

    public GeoLocations() {
    }
    public GeoLocations(@NonNull  OwnerType owner) {
        this.owner = owner;
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoLocations(@NonNull @Singular List<GeoLocation> values, @NonNull OwnerType owner) {
        this.values = values.stream().map(GeoLocation::clone).collect(Collectors.toList());
        this.owner = owner;
        //To help Hibernate understand the relationship we
        //explicitly set the parent!
        this.values.forEach(v -> v.setParent(this));
    }

    @Override
    @org.checkerframework.checker.nullness.qual.NonNull
    @XmlElement(name="geoLocation")
    @JsonIgnore
    public List<GeoLocation> getValues() {
        return super.getValues();
    }
    @Override
    public void setValues(List<GeoLocation> list) {
        super.setValues(list);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public GeoLocations clone() {
        return new GeoLocations(values.stream().map(GeoLocation::clone).collect(Collectors.toList()), owner);
    }
}
