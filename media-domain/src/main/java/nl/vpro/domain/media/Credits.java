package nl.vpro.domain.media;

import lombok.*;

import java.io.Serial;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.DomainObject;
import nl.vpro.domain.media.bind.CreditsDeserializer;
import nl.vpro.domain.media.gtaa.GTAAManaged;

/**
 * A container class for credits, linking the role to an actual entity.
 * <p>
 * For person this at the moment is an embedded entity, for other names, this should be a join with gtaa record.
 *
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(of = { "role" })
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlSeeAlso({
    Person.class,
    Name.class
})
@XmlType(name = "creditsType")
@XmlAccessorType(XmlAccessType.NONE)
@JsonDeserialize(using = CreditsDeserializer.class)
public abstract class Credits extends DomainObject implements Child<MediaObject>, GTAAManaged {

    @Serial
    private static final long serialVersionUID = 576943675942583696L;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected RoleType role = RoleType.UNDEFINED;


    @Column(name = "list_index",
        nullable = true
        // hibernate sucks incredibly https://stackoverflow.com/questions/41194606/why-does-hibernate-require-the-list-index-to-be-nullable
    )
    @XmlTransient
    @NotNull
    @Getter
    @Setter
    private Integer listIndex = 0;

    @ManyToOne(targetEntity = MediaObject.class, fetch = FetchType.LAZY)
    @XmlTransient
    protected MediaObject mediaObject;

    public static Credits copy(Credits credits, MediaObject mediaObject) {
        if (credits instanceof Person) {
            return Person.copy((Person) credits, mediaObject);
        } else if (credits instanceof Name) {
            return Name.copy((Name) credits, mediaObject);
        } else {
            throw new IllegalStateException();
        }
    }


    @Override
    public void setParent(MediaObject mo) {
        this.mediaObject = mo;
    }

    @Override
    public MediaObject getParent() {
        return this.mediaObject;
    }

    /**
     * return the 'objectType' of this Credits. Should probably correspond to the two relevant enums in nl.vpro.domain.gtaa.Scheme
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected String getObjectType() {
        return getClass().getSimpleName().toLowerCase();
    }

    /**
     * Returns the name of the credits. This is how it would be referred to by humans.
     */
    @Override
    public abstract String getName();

    /**
     * To better understand about which or what we are talking, these string may give some scope.
     */
    @Override
    public abstract List<String> getScopeNotes();

    /**
     * TODO: describe this?
     */
    public abstract Boolean getGtaaKnownAs();
}
