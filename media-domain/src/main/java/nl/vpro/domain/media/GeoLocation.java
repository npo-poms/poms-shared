package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.gtaa.Status;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meeuw.i18n.Region;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.gtaa.GTAAGeographicName;
import nl.vpro.domain.gtaa.persistence.EmbeddableGTAARecord;
import nl.vpro.domain.gtaa.persistence.EmbeddableGeographicName;
import nl.vpro.domain.media.support.MediaObjectOwnableListItem;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.validation.NoHtml;


/**
 * A wrapper around GTAA {@link GTAAGeographicName}
 * @author Giorgio Vinci
 * @since 5.11
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "geoLocationType")
@Getter
@Setter
public class GeoLocation extends DomainObject implements MediaObjectOwnableListItem<GeoLocation, GeoLocations>, Region {


    @ManyToOne(targetEntity = GeoLocations.class, fetch = FetchType.LAZY)
    @XmlTransient
    private GeoLocations parent;

    @NoHtml
    @XmlElement
    private String name;

    @NoHtml
    @XmlElement
    private String description;

    @Column(name= "role", nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected GeoRoleType role;

    @Embedded
    @XmlTransient
    private EmbeddableGeographicName gtaaRecord;

    public GeoLocation() {
    }

    @lombok.Builder(builderClassName = "Builder")
    public GeoLocation(@NonNull String name, @NonNull GeoRoleType role, String description, EmbeddableGeographicName gtaaRecord) {
        this.name = name;
        this.role = role;
        this.description = description;
        this.gtaaRecord = gtaaRecord;
    }

    @lombok.Builder(builderClassName = "Builder")
    public GeoLocation(Long id, @NonNull String name, @NonNull GeoRoleType role, String description, EmbeddableGeographicName gtaaRecord) {
        this(name, role, description, gtaaRecord);
        this.id = id;
    }

    public GeoLocation(GeoLocation source, GeoLocations parent) {
        this(source.getName(), source.getRole(), source.getDescription(), source.getGtaaRecord());
        this.parent = parent;
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoLocation(
            Long id,
            @NonNull String name,
            @NonNull OwnerType owner,
            @NonNull GeoRoleType role,
            String description,
            GeoLocations parent,
            EmbeddableGeographicName gtaaRecord) {
        this(id, name, role, description, gtaaRecord);
        this.parent = parent;
    }

    @XmlAttribute
    public Status getGtaaStatus() {
        return Optional.ofNullable(gtaaRecord)
                .map(EmbeddableGTAARecord::getStatus)
                .orElse(null);
    }


    @XmlAttribute
    public String getGtaaUri() {
        return Optional.ofNullable(gtaaRecord)
                .map(EmbeddableGTAARecord::getUri)
                .orElse(null);
    }
    public void setGtaaUri(String uri) {
        this.gtaaRecord = new EmbeddableGeographicName(uri, null);
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

        if(!Objects.equals(name, geoLocation.name)) {
            return false;
        }

        if(!Objects.equals(role, geoLocation.role)) {
            return false;
        }

        if(!Objects.equals(description, geoLocation.description)) {
            return false;
        }

        return Objects.equals(getGtaaUri(), geoLocation.getGtaaUri());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        String gtaaUri = getGtaaUri();
        result = 31 * result + (gtaaUri != null ? gtaaUri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("name", name)
            .append("relationType", role)
            .append("description", description)
            .append("gtaa_uri", getGtaaUri())
            .toString();
    }

    @Override
    public int compareTo(GeoLocation geoLocation) {
        if(!this.name.equals(geoLocation.name)) {
            return this.name.compareTo(geoLocation.name);
        }

        if(!this.role.equals(geoLocation.role)) {
            return this.role.compareTo(geoLocation.role);
        }

        if(description != null && geoLocation.description != null && !description.equals(geoLocation.description)) {
            return this.description.compareTo(geoLocation.description);
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

    public static class Builder {
        public Builder gtaaUri(String uri) {
            return gtaaRecord(EmbeddableGeographicName.builder().uri(uri).build());
        }
    }



}
