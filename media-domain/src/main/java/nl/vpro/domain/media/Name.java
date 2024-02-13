package nl.vpro.domain.media;

import lombok.*;

import java.io.Serial;
import java.net.URI;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.media.gtaa.*;


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
public class Name extends Credits implements GTAARecordManaged {

    @Serial
    private static final long serialVersionUID = -263374091559949040L;

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
        return GTAARecordManaged.super.getName();
    }
    @Override
    public void  setName(String name) {
        GTAARecordManaged.super.setName(name);
    }

    @XmlElement(name = "scopeNote")
    @JsonProperty("scopeNotes")
    @Override
    public List<String> getScopeNotes() {
        return GTAARecordManaged.super.getScopeNotes();
    }

    @Override
    public void setScopeNotes(List<String> scopeNotes) {
        GTAARecordManaged.super.setScopeNotes(scopeNotes);
    }

    @Override
    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return GTAARecordManaged.super.getGtaaStatus();
    }
    @Override
    public void setGtaaStatus(GTAAStatus status) {
        GTAARecordManaged.super.setGtaaStatus(status);
    }

    @Override
    @XmlAttribute
    public String  getGtaaUri() {
        return GTAARecordManaged.super.getGtaaUri();
    }
    @Override
    public void setGtaaUri(String uri) {
        GTAARecordManaged.super.setGtaaUri(uri);
    }


    /**
     * Always returns false, because we arranged that all redirects are simply resolved in the database.
     * <p>
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
