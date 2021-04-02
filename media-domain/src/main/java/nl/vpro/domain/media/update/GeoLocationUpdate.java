package nl.vpro.domain.media.update;

import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

import nl.vpro.domain.media.GeoLocation;
import nl.vpro.domain.media.GeoRoleType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "geoLocationUpdateType")
@XmlRootElement(name = "geoLocation")
public class GeoLocationUpdate {

    @XmlAttribute(required = true)
    @Getter
    private String gtaaUri;

    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    protected GeoRoleType role;

    public GeoLocationUpdate(String gtaaUri, GeoRoleType role) {
        this.gtaaUri = gtaaUri;
        this.role = role;
    }

    public GeoLocationUpdate(GeoLocation geoLocation) {
        this(geoLocation.getGtaaUri(), geoLocation.getRole());
    }

    public GeoLocationUpdate() {
        // needed for jaxb
    }

    public GeoLocation toGeoLocation() {
        return GeoLocation.builder().uri(gtaaUri).role(role).build();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("gtaaUri", gtaaUri)
            .append("role", role)
            .toString();
    }
}
