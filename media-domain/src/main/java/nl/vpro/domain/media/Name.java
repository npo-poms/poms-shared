package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.media.gtaa.GTAAStatus;


/**
 * Connects an  entry in GTAA with the scheme 'http://data.beeldengeluid.nl/gtaa/Namen' with a {@link MediaObject}.
 **
 *
 */
@Entity(name = "name")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "nameType")
@JsonDeserialize
@JsonPropertyOrder({
    "objectType",
    "name",
    "role"/*,
    "gtaaUri",
    "gtaaStatus"
    */

})
public class Name extends Credits  {

    @XmlElement
    @Getter
    @Setter
    protected String name;

    public Name() {

    }

    @lombok.Builder
    private Name(RoleType role, String name) {
        this.role = role;
        this.name = name;
    }

    public Name(Name source, MediaObject parent) {
        //this.gtaaRecord = source.gtaaRecord;
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

/*

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "gtaa_uri")
    @Getter
    @Setter
    private GTAARecord gtaaRecord = new GTAARecord();

*/
    @Override
    public String toString() {
        return Name.class.getSimpleName() + ":" + role + ":" + name;
    }


    @Override
    public String getGtaaUri() {
        return null;

    }

    @Override
    public GTAAStatus getGtaaStatus() {
        return null;

    }

    @Override
    public Boolean getGtaaKnownAs() {
        return null;

    }
}
