package nl.vpro.domain.i18n;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import javax.xml.XMLConstants;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.functional.TriFunction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.*;
import nl.vpro.domain.media.Email;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Editor;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.persistence.InstantToTimestampConverter;
import nl.vpro.xml.bind.InstantXmlAdapter;
import nl.vpro.xml.bind.LocaleAdapter;

import static nl.vpro.domain.TextualObjects.sorted;

/**
 * Contains the translations for the translatable fields of a {@link nl.vpro.domain.media.MediaObject}
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
@XmlRootElement(name = "media")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class MediaObjectTranslation implements
    LocalizedObject<TitleTranslation, DescriptionTranslation, WebsiteTranslation, TwitterRefTranslation, MediaObjectTranslation>,
    Identifiable<Long>,
    Accountable, Serializable {

    @Serial
    private static final long serialVersionUID = -2040558972295866861L;

    @Id
    @GeneratedValue
    @XmlAttribute
    protected Long id;

    @Column
    @XmlAttribute
    protected String mid;

    @Column(name = "lastModified")
    @Convert(converter = InstantToTimestampConverter.class)
    @XmlAttribute(name = "lastModified")
    @JsonProperty("lastModified")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @Setter
    protected Instant lastModifiedInstant;

    @Column(name = "creationDate")
    @Convert(converter = InstantToTimestampConverter.class)
    @XmlAttribute(name = "creationDate")
    @JsonProperty("creationDate")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @Setter
    protected Instant creationInstant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lastmodifiedby_principalid")
    @Setter
    protected Editor lastModifiedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "createdby_principalid")
    @Setter
    protected Editor createdBy;

    @Column
    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    protected Locale language;


    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.collection.Size.min}"),
    })
    @Valid
    @XmlElement(name = "title", required = true)
    @JsonProperty("titles")
    protected Set<TitleTranslation> titles = new TreeSet<>();


    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Valid
    @XmlElement(name = "description")
    @JsonProperty("descriptions")
    protected Set<DescriptionTranslation> descriptions = new TreeSet<>();

    @ManyToMany
    @Valid
    @JoinTable
    @XmlElement(name = "tag")
    @JsonProperty("tags")
    protected Set<Tag> tags = new TreeSet<>();


    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    @OrderColumn(name = "list_index")
    @XmlElement(name = "website")
    @JsonProperty("websites")
    protected List<WebsiteTranslation> websites = new ArrayList<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id")
    @OrderColumn(name = "list_index")
    @Valid
    @XmlElement(name = "twitter")
    @JsonProperty("twitter")
    protected List<TwitterRefTranslation> twitterRefs = new ArrayList<>();

    public MediaObjectTranslation(String mid, Locale locale) {
        this.mid = mid;
        this.language = locale;
    }

    public MediaObjectTranslation() {
    }


    @Override
    public SortedSet<TitleTranslation> getTitles() {
        return sorted(titles);

    }

    @Override
    public void setTitles(SortedSet<TitleTranslation> titles) {
        this.titles = titles;
        for (TitleTranslation t : titles) {
            t.setParent(this);
        }

    }

    @Override
    public TriFunction<String, OwnerType, TextualType, TitleTranslation> getOwnedTitleCreator() {
        return (value, ownerType, textualType) -> new TitleTranslation(MediaObjectTranslation.this, value, ownerType, textualType);
    }

    @Override
    public MediaObjectTranslation addTitle(@NonNull String title, @NonNull OwnerType owner,  @NonNull TextualType type) {
        final TitleTranslation existingTitle = findTitle(owner, type);

        if (existingTitle != null) {
            existingTitle.set(title);
        } else {
            TitleTranslation newObject = getOwnedTitleCreator().apply(title, owner, type);
            newObject.setParent(this);
            this.addTitle(newObject);
        }

        return this;
    }

    @Override
    public TriFunction<String, OwnerType, TextualType, DescriptionTranslation> getOwnedDescriptionCreator() {
        return (value, ownerType, textualType) -> new DescriptionTranslation(MediaObjectTranslation.this, value, ownerType, textualType);
    }


    @Override
    public SortedSet<DescriptionTranslation> getDescriptions() {
        return sorted(descriptions);

    }

    @Override
    public void setDescriptions(SortedSet<DescriptionTranslation> descriptions) {
        this.descriptions = descriptions;
        for (DescriptionTranslation d : descriptions) {
            d.setParent(this);
        }

    }

    @Override
    public MediaObjectTranslation addDescription(
        @NonNull String description, @NonNull OwnerType owner, @NonNull TextualType type) {
        final DescriptionTranslation existingDescription = findDescription(owner, type);

        if (existingDescription != null) {
            existingDescription.set(description);
        } else {
            DescriptionTranslation newObject = getOwnedDescriptionCreator().apply(description, owner, type);
            newObject.setParent(this);
            this.addDescription(newObject);
        }

        return this;

    }


    @Override
    public SortedSet<Tag> getTags() {
        return sorted(tags);

    }

    @Override
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public List<WebsiteTranslation> getWebsites() {
        return websites;

    }

    @Override
    public MediaObjectTranslation setWebsites(List<WebsiteTranslation> websites) {
        this.websites = websites;
        return self();

    }

    @Override
    public List<TwitterRefTranslation> getTwitterRefs() {
        return this.twitterRefs;

    }

    @Override
    public void setTwitterRefs(List<TwitterRefTranslation> twitterRefs) {
        this.twitterRefs = twitterRefs;
    }


    @Override
    public String toString() {
        return language + ":" + mid + ":" + getMainTitle();
    }
}
