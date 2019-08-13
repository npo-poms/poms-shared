package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meeuw.i18n.Region;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.GTAAGeoLocationRecord;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.domain.media.support.MediaObjectOwnableListItem;


/**
 * A wrapper around GTAA {@link GTAAGeoLocationRecord}
 * @author Giorgio Vinci
 * @since 5.11
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "geoLocationType", propOrder = {
        "name",
        "description",
        "gtaaUri",
        "gtaaStatus",
        "role"
})
@Getter
@Setter
public class GeoLocation extends DomainObject implements MediaObjectOwnableListItem<GeoLocation, GeoLocations>, Region {


    @ManyToOne(targetEntity = GeoLocations.class, fetch = FetchType.LAZY)
    @XmlTransient
    private GeoLocations parent;

    @Column(name= "role", nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected GeoRoleType role;

    @XmlTransient
    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, targetEntity = GTAAGeoLocationRecord.class)
    @JoinColumn(name = "gtaa_uri")
    private GTAAGeoLocationRecord gtaaRecord = new GTAAGeoLocationRecord();

    public GeoLocation() {
    }

    @lombok.Builder
    private GeoLocation(@NonNull String name,
                       String scopeNotes,
                       @NonNull String gtaaUri,
                       @NonNull GTAAStatus gtaaStatus,
                       @NonNull GeoRoleType role
    ) {
        this.role = role;
        this.gtaaRecord = GTAAGeoLocationRecord.builder().name(name).scopeNotes(scopeNotes).uri(gtaaUri).status(gtaaStatus).build();
    }

    public GeoLocation(Long id, @NonNull GeoRoleType role, @NonNull GTAAGeoLocationRecord gtaaRecord) {
        this(role, gtaaRecord);
        this.id = id;
    }

    public GeoLocation(@NonNull GeoRoleType role, @NonNull GTAAGeoLocationRecord gtaaRecord) {
        this.role = role;
        this.gtaaRecord = gtaaRecord;
    }

    public GeoLocation(GeoLocation source, GeoLocations parent) {
        this(source.getRole(), source.getGtaaRecord());
        this.parent = parent;
    }


    @Override
    @XmlElement
    public String getName() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAAGeoLocationRecord::getName)
                .orElse(null);
    }
    public void setName(String name) {
        this.gtaaRecord.setName(name);
    }

    @XmlElement
    public String getDescription() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAAGeoLocationRecord::getScopeNotes)
                .orElse(null);
    }
    public void setDescription(String description) {
        this.gtaaRecord.setScopeNotes(description);
    }

    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAAGeoLocationRecord::getStatus)
                .orElse(null);
    }
    public void setGtaaStatus(GTAAStatus gtaaStatus) {
        this.gtaaRecord.setStatus(gtaaStatus);
    }


    @XmlAttribute
    public String getGtaaUri() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAAGeoLocationRecord::getUri)
                .orElse(null);
    }
    public void setGtaaUri(String uri) {
        this.gtaaRecord.setUri(uri);
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o)) {
            return true;
        }
        if(!(o instanceof GeoLocation)) {
            return false;
        }

        GeoLocation geoLocation = (GeoLocation)o;

        if(!Objects.equals(getName(), geoLocation.getName())) {
            return false;
        }

        if(!Objects.equals(role, geoLocation.role)) {
            return false;
        }

        if(!Objects.equals(getDescription(), geoLocation.getDescription())) {
            return false;
        }

        return Objects.equals(getGtaaUri(), geoLocation.getGtaaUri());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getGtaaUri() != null ? getGtaaUri().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("name", getName())
            .append("relationType", role)
            .append("description", getDescription())
            .append("gtaa_uri", getGtaaUri())
            .toString();
    }

    @Override
    public int compareTo(GeoLocation geoLocation) {
        if(!this.getName().equals(geoLocation.getName())) {
            return this.getName().compareTo(geoLocation.getName());
        }

        if(!this.role.equals(geoLocation.role)) {
            return this.role.compareTo(geoLocation.role);
        }

        if(getDescription() != null && geoLocation.getDescription() != null && !getDescription().equals(geoLocation.getDescription())) {
            return this.getDescription().compareTo(geoLocation.getDescription());
        }

        if(getGtaaUri() != null && geoLocation.getGtaaUri() != null && !getGtaaUri().equals(geoLocation.getGtaaUri())) {
            return this.getGtaaUri().compareTo(geoLocation.getGtaaUri());
        }

        return (getGtaaUri() != null) ? 1 : -1;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public GeoLocation clone() {
        return new GeoLocation(this, parent);

    }

    @Override
    public String getCode() {
        return getGtaaUri();

    }

    @Override
    public Type getType() {
        return Type.UNDEFINED;

    }

}
