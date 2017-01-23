package nl.vpro.domain.i18n;

import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.TextualObject;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

import static nl.vpro.domain.TextualObjects.sorted;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
public class MediaObjectTranslation implements TextualObject<TitleTranslation, DescriptionTranslation, MediaObjectTranslation>, Identifiable<Long> {

    @Id
    @GeneratedValue
    protected Long id;


    @Column
    protected String mid;

    @Column
    protected Locale locale;


    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.collection.Size.min}"),
    })
    @Valid
    protected Set<TitleTranslation> titles = new TreeSet<>();


    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    @Valid
    protected Set<DescriptionTranslation> descriptions = new TreeSet<>();


    public MediaObjectTranslation(String mid, Locale locale) {
        this.mid = mid;
        this.locale = locale;
    }

    public MediaObjectTranslation() {
    }


    @Override
    public Locale getLocale() {
        return locale;

    }
    public void setLocale(Locale locale) {
        this.locale = locale;

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
}
