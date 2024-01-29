package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.net.URI;
import java.util.*;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.PersonInterface;
import nl.vpro.domain.media.gtaa.EmbeddablePerson;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.jackson2.Views;
import nl.vpro.validation.NoHtml;

@Getter
@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "personType",
    propOrder = {
        "givenName",
        "familyName",
        "gtaaUri",
        "gtaaStatus"
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

    @Serial
    private static final long serialVersionUID = 6033483875674065456L;

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
    @Override
    public void setName(String name) {
        if (name != null) {
            String[] split = name.split("\\s*,\\s*", 2);
            if (split.length == 1) {
                setGivenName("");
                setFamilyName(name);
            } else {
                setGivenName(split[1]);
                setFamilyName(split[0]);
            }
        } else {
            setFamilyName(null);
            setGivenName(null);
        }
    }


    @Override
    public List<String> getScopeNotes() {
        // TODO
        return null;
    }

    @Override
    public void setScopeNotes(List<String> scopeNotes) {
        // TODO
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
        if(!(o instanceof Person person)) {
            return false;
        }

        if (! Objects.equals(role, person.role)) {
            return false;
        }

        boolean considerGtaa = getGtaaUri() != null && person.getGtaaUri() != null;

        if (considerGtaa) {
            return Objects.equals(getGtaaUri(), person.getGtaaUri());
        } else {
            if(! Objects.equals(familyName, person.getFamilyName())) {
                return false;
            }
            if(! Objects.equals(givenName, person.getGivenName())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id == null ? 1 : id.hashCode();
        result = 31 * result + (role != null ? role.hashCode() : 0);
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
