package nl.vpro.domain.media;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.fortuna.ical4j.model.property.Geo;
import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.validation.NoHtml;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;

import javax.annotation.Nonnull;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.util.Objects;
import java.util.Optional;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "geoNameType")
@Getter
@Setter
public class GeoName extends DomainObject implements Child<GeoNames>, Comparable<GeoName>{

    public static GeoName copy(GeoName source) {
        return copy(source, source.parent);
    }

    public static GeoName copy(GeoName source, GeoNames parent) {
        if (source == null) {
            return null;
        }

        return new GeoName(source, parent);
    }

    @ManyToOne(targetEntity = GeoNames.class, fetch = FetchType.LAZY)
    @XmlTransient
    private GeoNames parent;

    @NoHtml
    @XmlElement
    private String name;

    @NoHtml
    @XmlElement
    private String description;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected GeoRelationType relationType;

    @Embedded
    @XmlTransient
    private GTAARecord gtaaRecord;

    public GeoName() {
    }

    @lombok.Builder(builderClassName = "Builder")
    public GeoName(@NonNull String name, @NonNull GeoRelationType relationType, String description) {
        this.name = name;
        this.relationType = relationType;
        this.description = description;
    }

    public GeoName(Long id, @NonNull String name, @NonNull GeoRelationType relationType, String description, GTAARecord gtaaRecord) {
        this(name, relationType, description);
        this.id = id;
        this.gtaaRecord = gtaaRecord;
    }

    public GeoName(GeoName source, GeoNames parent) {
        this(source.getName(), source.getRelationType(), source.getDescription());
        this.gtaaRecord = source.gtaaRecord;
        this.parent = parent;
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoName(
            Long id,
            @NonNull String name,
            @NonNull OwnerType owner,
            @Nonnull GeoRelationType relationType,
            String description,
            GeoNames parent,
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
        if(!(o instanceof GeoName)) {
            return false;
        }

        GeoName geoName = (GeoName)o;

        if(!Objects.equals(name, geoName.name)) {
            return false;
        }

        if(!Objects.equals(relationType, geoName.relationType)) {
            return false;
        }

        if(!Objects.equals(description, geoName.description)) {
            return false;
        }

        return Objects.equals(getGtaaUri(), geoName.getGtaaUri());
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
    public int compareTo(GeoName geoName) {
        if(!this.name.equals(geoName.name)) {
            return this.name.compareTo(geoName.name);
        }

        if(!this.relationType.equals(geoName.relationType)) {
            return this.relationType.compareTo(geoName.relationType);
        }

        if(description != null && geoName.description != null && !description.equals(geoName.description)) {
            return this.description.compareTo(geoName.description);
        }

        if(getGtaaUri() != null && geoName.getGtaaUri() != null && !getGtaaUri().equals(geoName.getGtaaUri())) {
            return this.getGtaaUri().compareTo(geoName.getGtaaUri());
        }

        return (getGtaaUri() != null) ? 1 : -1;
    }

    public static class Builder {
        public Builder gtaaUri(String uri) {
            return gtaaRecord(GTAARecord.builder().uri(uri).build());
        }
    }



}
