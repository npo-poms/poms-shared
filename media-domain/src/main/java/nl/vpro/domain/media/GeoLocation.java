package nl.vpro.domain.media;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.gtaa.GTAARecord;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meeuw.i18n.regions.Region;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.domain.media.support.MediaObjectOwnableListItem;


/**
 * A wrapper around GTAA {@link GTAARecord}
 * @author Giorgio Vinci
 * @since 5.11
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoLocationType", propOrder = {
        "name",
        "scopeNotes",
        "gtaaUri",
        "gtaaStatus",
        "role"
})

public class GeoLocation extends DomainObject implements MediaObjectOwnableListItem<GeoLocation, GeoLocations>, Region {

    @ManyToOne(targetEntity = GeoLocations.class, fetch = FetchType.LAZY)
    @XmlTransient
    @Getter
    @Setter
    private GeoLocations parent;

    @Column(name= "role", nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private GeoRoleType role;

    @XmlTransient
    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, targetEntity = GTAARecord.class)
    @JoinColumn(name = "gtaa_id")
    @Getter
    @Setter
    private GTAARecord gtaaRecord = new GTAARecord();

    public static GeoLocation of(GeoRoleType role, GTAARecord gtaaRecord) {
        return new GeoLocation(role, gtaaRecord);
    }

    public static GeoLocation subject(GTAARecord gtaaRecord) {
        return of(GeoRoleType.SUBJECT, gtaaRecord);
    }

    public static GeoLocation producedIn(GTAARecord gtaaRecord) {
        return of(GeoRoleType.PRODUCED_IN, gtaaRecord);
    }

    public static GeoLocation recordedIn(GTAARecord gtaaRecord) {
        return of(GeoRoleType.RECORDED_IN, gtaaRecord);
    }

    public GeoLocation() {
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoLocation(Long id,
                        @NonNull String name,
                        @Singular List<String> scopeNotes,
                        @NonNull URI uri,
                        GTAAStatus gtaaStatus,
                        @NonNull GeoRoleType role) {

        this.id = id;
        this.role = role;
        this.gtaaRecord = GTAARecord.builder()
            .name(name)
            .scopeNotes(scopeNotes)
            .uri(uri)
            .status(gtaaStatus)
            .build();
    }

    private GeoLocation(@NonNull GeoRoleType role, @NonNull GTAARecord gtaaRecord) {
        this.role = role;
        this.gtaaRecord = gtaaRecord;
    }

    private GeoLocation(GeoLocation source, GeoLocations parent) {
        this(source.getRole(), source.gtaaRecord);
        this.parent = parent;
    }

    @Override
    @XmlElement
    public String getName() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getName)
                .orElse(null);
    }

    public void setName(String name) {
        this.gtaaRecord.setName(name);
    }

    @XmlElement(name = "scopeNote")
    @JsonProperty("scopeNotes")
    public List<String> getScopeNotes() {
        return gtaaRecord.getScopeNotes();
    }

    public void setScopeNotes(List<String> scopeNotes) {
        this.gtaaRecord.setScopeNotes(scopeNotes);
    }

    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getStatus)
                .orElse(null);
    }

    public void setGtaaStatus(GTAAStatus gtaaStatus) {
        this.gtaaRecord.setStatus(gtaaStatus);
    }

    @XmlAttribute
    public URI getGtaaUri() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getUri)
                .orElse(null);
    }

    public void setGtaaUri(URI uri) {
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

        //Null or empty list are semantically equals.
        if(!Objects.equals(getScopeNotes(), geoLocation.getScopeNotes()) && !areEmpty(getScopeNotes(), geoLocation.getScopeNotes())) {
            return false;
        }

        return Objects.equals(getGtaaUri(), geoLocation.getGtaaUri());
    }

    public static boolean areEmpty(List<String> a, List<String> b){
       return (a == null || a.isEmpty()) && (b == null || b.isEmpty());
    }

    @Override
    public int hashCode() {

        int result = super.hashCode();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (getScopeNotes() != null && !getScopeNotes().isEmpty() ? getScopeNotes().hashCode() : 0);
        result = 31 * result + (getGtaaUri() != null ? getGtaaUri().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("name", getName())
            .append("relationType", role)
            .append("scopeNotes", getScopeNotes())
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
        return getGtaaUri().toString();
    }

    @Override
    public Type getType() {
        return Type.UNDEFINED;
    }

    public static class Builder {

        public Builder gtaaUri(String uri) {
            return uri(URI.create(uri));
        }
    }
}
