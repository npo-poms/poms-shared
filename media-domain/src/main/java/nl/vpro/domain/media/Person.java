package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.*;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.PersonInterface;
import nl.vpro.domain.media.gtaa.EmbeddablePerson;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.jackson2.Views;
import nl.vpro.validation.NoHtml;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "personType",
    propOrder = {
        "givenName",
        "familyName"
    })
@JsonDeserialize
@JsonPropertyOrder({
    "objectType",
    "givenName",
    "familyName",
    "role",
    "gtaaUri",
    "gtaaStatus"

})
public class Person extends Credits implements PersonInterface {

    public static Person copy(Person source) {
        return copy(source, source.mediaObject);
    }

    public static Person copy(Person source,  MediaObject parent) {
        if (source == null) {
            return null;
        }

        return new Person(source, parent);
    }

    @NoHtml
    @XmlElement
    @Getter
    @Setter
    protected String givenName;

    @NoHtml
    @XmlElement
    @Getter
    @Setter
    protected String familyName;

    @Embedded
    @XmlTransient
    @Getter
    @Setter
    protected EmbeddablePerson gtaaInfo;

    public Person() {
    }

    public Person(String givenName, String familyName) {
        this.givenName = givenName;
        this.familyName = familyName;
    }

    public Person(String givenName, String familyName, RoleType role) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.role = role;
    }

    public Person(Long id, String givenName, String familyName, RoleType role) {
        this(givenName, familyName, role);
        this.id = id;
    }

    public Person(Person source) {
        this(source, source.mediaObject);
    }

    public Person(Person source, MediaObject parent) {
        this(source.getGivenName(), source.getFamilyName(), source.getRole());
        this.gtaaInfo = source.gtaaInfo;
        this.mediaObject = parent;
    }


    @lombok.Builder(builderClassName = "Builder")
    private Person(
        Long id,
        String givenName,
        String familyName,
        RoleType role,
        MediaObject mediaObject,
        GTAAStatus gtaaStatus,
        URI uri,
        Boolean gtaaKnownAs,
        String name
        ) {
        this.id = id;
        this.role = role;
        setName(name);
        if (givenName != null) {
            this.givenName = givenName;
        }
        if (familyName != null) {
            this.familyName = familyName;
        }
        this.mediaObject = mediaObject;
        if (uri != null) {
            this.gtaaInfo = new EmbeddablePerson();
            this.gtaaInfo.setStatus(gtaaStatus);
            this.gtaaInfo.setUri(uri.toString());
            if (gtaaKnownAs != null) {
                this.gtaaInfo.setKnownAs(gtaaKnownAs);
            }
        } else {
            if (gtaaStatus != null) {
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * Sets both the given name and the family name by splitting the String on a comma.
     */
    public void setName(String name) {
        String[] parsed = PersonInterface.parseName(name);
        setFamilyName(parsed[0]);
        setGivenName(parsed[1]);
    }

    @Deprecated
    public MediaObject getMediaObject() {
        return mediaObject;
    }

    @Deprecated
    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }


    @Override
    public List<String> getScopeNotes() {
        // TODO
        return null;
    }

    @Override
    @XmlAttribute
    public String getGtaaUri() {
        return Optional.ofNullable(gtaaInfo)
                .map(EmbeddablePerson::getUri)
                .orElse(null);
    }

    @Override
    public void setGtaaUri(String uri) {
        if (this.gtaaInfo == null) {
            this.gtaaInfo = new EmbeddablePerson();
        }
        this.gtaaInfo.setUri(uri);
    }

    @Override
    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return Optional.ofNullable(gtaaInfo)
                .map(EmbeddablePerson::getStatus)
                .orElse(null);
    }

    @Override
    public void setGtaaStatus(GTAAStatus status) {
        if (this.gtaaInfo == null) {
            this.gtaaInfo = new EmbeddablePerson();
        }
        this.gtaaInfo.setStatus(status);
    }

    @Override
    public Boolean getGtaaKnownAs() {
        return Optional.ofNullable(gtaaInfo)
                .map(EmbeddablePerson::isKnownAs)
                .orElse(null);
    }

    public void setGtaaKnownAs(Boolean knownAs) {
        if (this.gtaaInfo == null) {
            this.gtaaInfo = new EmbeddablePerson();
        }
        this.gtaaInfo.setKnownAs(knownAs);
    }

    @Override
    public boolean equals(Object o) {
        if(super.equals(o)) {
            return true;
        }
        if(!(o instanceof Person)) {
            return false;
        }

        Person person = (Person)o;

        if(familyName != null ? !familyName.equals(person.familyName) : person.familyName != null) {
            return false;
        }
        if(givenName != null ? !givenName.equals(person.givenName) : person.givenName != null) {
            return false;
        }
        if(role != person.role) {
            return false;
        }
        return Objects.equals(getGtaaUri(), person.getGtaaUri());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        String gtaaUri = getGtaaUri();
        result = 31 * result + (gtaaUri != null ? gtaaUri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("givenName", givenName)
            .append("familyName", familyName)
            .append("role", role)
            .append("gtaa_uri", getGtaaUri())
            .toString();
    }

    @Override
    @JsonProperty
    @JsonView({Views.Publisher.class})
    public String getName() {
        return PersonInterface.super.getName();
    }


    public static class Builder {
        public Builder gtaaUri(String uri) {
            return uri(uri == null ? null : URI.create(uri));
        }
    }



}
