package nl.vpro.domain.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.gtaa.GTAAStatus;


/**
 * Connects an  entry in GTAA with the scheme 'http://data.beeldengeluid.nl/gtaa/Namen' with a {@link MediaObject}.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(of = { "gtaaRecord" }, callSuper = true)
@EqualsAndHashCode(of = { "gtaaRecord" }, callSuper = false)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "nameType", propOrder = {"name", "scopeNotes", "gtaaUri", "gtaaStatus"})
@JsonDeserialize
@JsonPropertyOrder({
    "objectType",
    "role",
    "name",
    "scopeNotes",
    "gtaaUri",
    "gtaaStatus"
})
public class Name extends Credits  {

    @XmlTransient
    @ManyToOne(optional = false)
    @JoinColumn(name = "gtaa_uri", nullable = false)
    private GTAARecord gtaaRecord;

    public Name() {
        gtaaRecord = new GTAARecord();
    }

    @lombok.Builder
    private Name(Long id,
                 String name,
                 @Singular List<String> scopeNotes,
                 @NonNull String uri,
                 GTAAStatus gtaaStatus,
                 RoleType role) {

        this.id = id;
        this.role = role;
        this.gtaaRecord = GTAARecord.builder()
            .name(name)
            .scopeNotes(scopeNotes)
            .uri(uri)
            .status(gtaaStatus)
            .build();
    }

    public Name(Name source, MediaObject parent) {
        this.gtaaRecord = source.gtaaRecord;
        this.mediaObject = parent;
    }

    public static Name copy(Name source) {
        return copy(source, source.mediaObject);
    }

    public static Name copy(Name source, MediaObject parent) {
        if (source == null) {
            return null;
        }
        return new Name(source, parent);
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

    @Override
    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return gtaaRecord.getStatus();
    }

    public void setGtaaStatus(GTAAStatus gtaaStatus) {
        this.gtaaRecord.setStatus(gtaaStatus);
    }

    @Override
    @XmlAttribute
    public String getGtaaUri() {
        return gtaaRecord.getUri();
    }

    public void setGtaaUri(String uri) {
        gtaaRecord.setUri(uri);
    }

    @Override
    public Boolean getGtaaKnownAs() {
        return null; // unknown
    }
}
