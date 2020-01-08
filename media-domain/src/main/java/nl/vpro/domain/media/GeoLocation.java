package nl.vpro.domain.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.meeuw.i18n.regions.Region;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.domain.media.support.MediaObjectOwnableListItem;

/**
 * A GeoLocation is a wrapper around a GTAARecord linking it ot a GeoLocations record.
 */
@Entity
@Getter
@Setter
@ToString(of = { "gtaaRecord", "role" })
@EqualsAndHashCode(of = { "gtaaRecord" }, callSuper = false)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoLocationType", propOrder = {"name", "scopeNotes", "gtaaUri", "gtaaStatus", "role"})
public class GeoLocation extends DomainObject implements MediaObjectOwnableListItem<GeoLocation, GeoLocations>, Region {

    @ManyToOne(fetch = FetchType.LAZY)
    @XmlTransient
    private GeoLocations parent;

    @Column(name= "role", nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    private GeoRoleType role;

    @XmlTransient
    @ManyToOne(optional = false)
    @JoinColumn(name = "gtaa_uri", nullable = false)
    private GTAARecord gtaaRecord;

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
        gtaaRecord = new GTAARecord();
    }

    @lombok.Builder
    private GeoLocation(Long id,
                        @NonNull String name,
                        @Singular List<String> scopeNotes,
                        @NonNull String uri,
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
        return gtaaRecord.getName();
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

        if (scopeNotes != null) {
            gtaaRecord.setScopeNotes(scopeNotes);
        }
        else {
            gtaaRecord.setScopeNotes(new ArrayList<>());
        }
    }

    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return gtaaRecord.getStatus();
    }

    public void setGtaaStatus(GTAAStatus gtaaStatus) {
        this.gtaaRecord.setStatus(gtaaStatus);
    }

    @XmlAttribute
    public String getGtaaUri() {
        return gtaaRecord.getUri();
    }

    public void setGtaaUri(String uri) {
        gtaaRecord.setUri(uri);
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
        return getGtaaUri();
    }

    @Override
    public Type getType() {
        return Type.UNDEFINED;
    }
}
