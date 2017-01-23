package nl.vpro.domain.i18n;

import java.util.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.LocalizedObject;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.xml.bind.LocaleAdapter;

import static nl.vpro.domain.TextualObjects.sorted;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
public class MediaObjectTranslation implements LocalizedObject<TitleTranslation, DescriptionTranslation, WebsiteTranslation, TwitterRefTranslation, MediaObjectTranslation>, Identifiable<Long> {

    @Id
    @GeneratedValue
    @XmlAttribute
    protected Long id;

    @Column
    @XmlAttribute
    protected String mid;

    @Column
    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    protected Locale language;


    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.collection.Size.min}"),
    })
    @Valid
    protected Set<TitleTranslation> titles = new TreeSet<>();


    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Valid
    protected Set<DescriptionTranslation> descriptions = new TreeSet<>();

    @ManyToMany
    @Valid
    @JoinTable
    protected Set<Tag> tags = new TreeSet<>();


    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    protected List<WebsiteTranslation> websites = new ArrayList<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @Valid
    protected List<TwitterRefTranslation> twitterRefs = new ArrayList<>();

    public MediaObjectTranslation(String mid, Locale locale) {
        this.mid = mid;
        this.language = locale;
    }

    public MediaObjectTranslation() {
    }


    public Locale getLanguage() {
        return language;

    }
    public void setLanguage(Locale language) {
        this.language = language;

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
    public MediaObjectTranslation addTitle(String title, OwnerType owner, TextualType type) {
        final TitleTranslation existingTitle = findTitle(owner, type);

        if (existingTitle != null) {
            existingTitle.set(title);
        } else {
            TitleTranslation newObject = new TitleTranslation(title, owner, type);
            newObject.setParent(this);
            this.addTitle(newObject);
        }

        return this;
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
    public MediaObjectTranslation addDescription(String description, OwnerType owner, TextualType type) {
        final DescriptionTranslation existingDescription = findDescription(owner, type);

        if (existingDescription != null) {
            existingDescription.set(description);
        } else {
            DescriptionTranslation newObject = new DescriptionTranslation(description, owner, type);
            newObject.setParent(this);
            this.addDescription(newObject);
        }

        return this;

    }


    @Override
    public Long getId() {
        return id;
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
}
