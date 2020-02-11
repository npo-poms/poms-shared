package nl.vpro.domain.media;

import lombok.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.gtaa.GTAAStatus;


/**
 * Connects an  entry in GTAA with the scheme 'http://data.beeldengeluid.nl/gtaa/Namen' with a {@link MediaObject}.
 */
@Entity
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

    @lombok.Builder(builderClassName = "Builder")
    private Name(Long id,
                 String name,
                 @Singular List<String> scopeNotes,
                 @NonNull URI _uri,
                 GTAAStatus gtaaStatus,
                 RoleType role) {

        this.id = id;
        this.role = role;
        this.gtaaRecord = GTAARecord.builder()
            .name(name)
            .scopeNotes(scopeNotes)
            .uri(_uri.toString())
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
    @Override
    public List<String> getScopeNotes() {
        return gtaaRecord.getScopeNotes();
    }


    public void setScopeNotes(List<String> scopeNotes) {
        if (scopeNotes != null) {
            gtaaRecord.setScopeNotes(scopeNotes);
        } else {
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

    /**
     * Always returns false, because we arranged that all redirects are simply resolved in the database.
     *
     * (TODO: right?)
     */
    @Override
    public Boolean getGtaaKnownAs() {
        return false;
    }

    public static class Builder {

        URI _uri;

        public Builder uri(@Pattern(regexp = "http://data\\.beeldengeluid\\.nl/gtaa/[0-9]+") String u) {
            return _uri(URI.create(u));
        }
        public Builder uri(URI u) {
            return _uri(u);
        }
        private Builder _uri(URI u) {
            this._uri = u;
            return this;
        }
    }
}
