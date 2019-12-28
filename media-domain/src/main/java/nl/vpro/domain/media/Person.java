package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import nl.vpro.domain.PersonInterface;
import nl.vpro.domain.media.gtaa.EmbeddablePerson;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.validation.NoHtml;

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "personType",
    propOrder = {
        "givenName",
        "familyName"
    })
@JsonTypeName("person")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "objectType")
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
    protected EmbeddablePerson gtaaRecord;

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
        this.gtaaRecord = source.gtaaRecord;

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
        Boolean gtaaKnownAs
        ) {
        this(id, givenName, familyName, role);
        this.mediaObject = mediaObject;
        if (uri != null) {
            this.gtaaRecord = new EmbeddablePerson();
            this.gtaaRecord.setStatus(gtaaStatus);
            this.gtaaRecord.setUri(uri.toString());
            if (gtaaKnownAs != null) {
                this.gtaaRecord.setKnownAs(gtaaKnownAs);
            }
        } else {
            if (gtaaStatus != null) {
                throw new IllegalArgumentException();
            }
        }

    }

    /**
     * Sets both the given name and the family name by splitting the String.
     */
    public void setName(String name) {
        String[] split = name.split("\\s+", 2);
        if(split.length == 1) {
            setGivenName("");
            setFamilyName(name);
        } else {
            setGivenName(split[0]);
            setFamilyName(split[1]);
        }
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
    @XmlAttribute
    public String getGtaaUri() {
        return Optional.ofNullable(gtaaRecord)
                .map(EmbeddablePerson::getUri)
                .orElse(null);
    }
    public void setGtaaUri(String uri) {
        if (this.gtaaRecord == null) {
            this.gtaaRecord = new EmbeddablePerson();
        }
        this.gtaaRecord.setUri(uri);
    }


    @XmlAttribute
    public GTAAStatus getGtaaStatus() {
        return Optional.ofNullable(gtaaRecord)
                .map(EmbeddablePerson::getStatus)
                .orElse(null);
    }
    public void setGtaaStatus(GTAAStatus status) {
        if (this.gtaaRecord == null) {
            this.gtaaRecord = new EmbeddablePerson();
        }
        this.gtaaRecord.setStatus(status);
    }



    public Boolean getGtaaKnownAs() {
        return Optional.ofNullable(gtaaRecord)
                .map(EmbeddablePerson::isKnownAs)
                .orElse(null);
    }
    public void setGtaaKnownAs(Boolean knownAs) {
        if (this.gtaaRecord == null) {
            this.gtaaRecord = new EmbeddablePerson();
        }
        this.gtaaRecord.setKnownAs(knownAs);
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


    public static class Builder {
        public Builder gtaaUri(String uri) {
            return uri(uri == null ? null : URI.create(uri));
        }
    }



}
