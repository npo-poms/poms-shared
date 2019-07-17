package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.validation.NoHtml;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "geoLocationType")
@Getter
@Setter
public class GeoLocation extends DomainObject implements Child<GeoLocations>, Comparable<GeoLocation>{

    public static GeoLocation copy(GeoLocation source) {
        return copy(source, source.parent);
    }

    public static GeoLocation copy(GeoLocation source, GeoLocations parent) {
        if (source == null) {
            return null;
        }

        return new GeoLocation(source, parent);
    }

    @ManyToOne(targetEntity = GeoLocations.class, fetch = FetchType.LAZY)
    @XmlTransient
    private GeoLocations parent;

    @NoHtml
    @XmlElement
    private String name;

    @NoHtml
    @XmlElement
    private String description;

    @Column(name= "relation_type", nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected GeoRelationType relationType;

    @Embedded
    @XmlTransient
    private GTAARecord gtaaRecord;

    public GeoLocation() {
    }

    @lombok.Builder(builderClassName = "Builder")
    public GeoLocation(@NonNull String name, @NonNull GeoRelationType relationType, String description) {
        this.name = name;
        this.relationType = relationType;
        this.description = description;
    }

    public GeoLocation(Long id, @NonNull String name, @NonNull GeoRelationType relationType, String description, GTAARecord gtaaRecord) {
        this(name, relationType, description);
        this.id = id;
        this.gtaaRecord = gtaaRecord;
    }

    public GeoLocation(GeoLocation source, GeoLocations parent) {
        this(source.getName(), source.getRelationType(), source.getDescription());
        this.gtaaRecord = source.gtaaRecord;
        this.parent = parent;
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoLocation(
            Long id,
            @NonNull String name,
            @NonNull OwnerType owner,
            @NonNull GeoRelationType relationType,
            String description,
            GeoLocations parent,
            GTAARecord gtaaRecord) {
        this(id, name, relationType, description, gtaaRecord);
        this.parent = parent;
    }

    @XmlAttribute
    public String getGtaaUri() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getUri)
                .orElse(null);
    }
    public void setGtaaUri(String uri) {
        this.gtaaRecord = new GTAARecord(uri, null);
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

        if(!Objects.equals(relationType, geoLocation.relationType)) {
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
        result = 31 * result + (relationType != null ? relationType.hashCode() : 0);
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
            .append("relationType", relationType)
            .append("description", description)
            .append("gtaa_uri", getGtaaUri())
            .toString();
    }

    @Override
    public int compareTo(GeoLocation geoLocation) {
        if(!this.name.equals(geoLocation.name)) {
            return this.name.compareTo(geoLocation.name);
        }

        if(!this.relationType.equals(geoLocation.relationType)) {
            return this.relationType.compareTo(geoLocation.relationType);
        }

        if(description != null && geoLocation.description != null && !description.equals(geoLocation.description)) {
            return this.description.compareTo(geoLocation.description);
        }

        if(getGtaaUri() != null && geoLocation.getGtaaUri() != null && !getGtaaUri().equals(geoLocation.getGtaaUri())) {
            return this.getGtaaUri().compareTo(geoLocation.getGtaaUri());
        }

        return (getGtaaUri() != null) ? 1 : -1;
    }

    public static class Builder {
        public Builder gtaaUri(String uri) {
            return gtaaRecord(GTAARecord.builder().uri(uri).build());
        }
    }



}
