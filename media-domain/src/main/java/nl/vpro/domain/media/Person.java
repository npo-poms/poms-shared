package nl.vpro.domain.media;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.validation.NoHtml;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personType",
    propOrder = {
        "givenName",
        "familyName"
    })
public class Person extends DomainObject {


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
    protected String givenName;

    @NoHtml
    @XmlElement
    protected String familyName;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    protected RoleType role;

    @Column(name = "list_index",
        nullable = true // hibernate sucks incredibly
    )
    @XmlTransient
    @NotNull
    private Integer listIndex = 0;

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    protected MediaObject mediaObject;

    @Embedded
    @XmlTransient
    protected GTAARecord gtaaRecord;

    public Person() {
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


    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String value) {
        this.givenName = value;
    }

    public String getFamilyName() {
        return familyName;
    }


    public void setFamilyName(String value) {
        this.familyName = value;
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

    /**
     * Gets the value of the role property.
     *
     * @return possible object is
     *         {@link RoleType }
     */
    public RoleType getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value allowed object is
     *              {@link RoleType }
     */
    public void setRole(RoleType value) {
        this.role = value;
    }

    public MediaObject getMediaObject() {
        return mediaObject;
    }

    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    public Integer getListIndex() {
        return listIndex;
    }

    public void setListIndex(Integer listIndex) {
        this.listIndex = listIndex;
    }

    public GTAARecord getGtaaRecord() {
        return gtaaRecord;
    }

    public void setGtaaRecord(GTAARecord gtaaRecord) {
        this.gtaaRecord = gtaaRecord;
    }

    @XmlAttribute
    public String getGtaaUri() {
        return Optional.ofNullable(gtaaRecord)
                .map(GTAARecord::getUri)
                .orElse(null);
    }
    public void setGtaaUri(String uri) {
        this.gtaaRecord= new GTAARecord(uri, null);
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
}
