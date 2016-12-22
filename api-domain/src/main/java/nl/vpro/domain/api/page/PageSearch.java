package nl.vpro.domain.api.page;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import nl.vpro.domain.api.*;
import nl.vpro.domain.page.*;
import nl.vpro.domain.user.Broadcaster;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.sql.Date;
import java.time.Instant;
import java.util.function.Predicate;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagesSearchType",
    propOrder = {
        // Intellij warnings are incorrect since parent class is @XmlTransient
        "text", "broadcasters", "types", "portals", "sections", "genres", "tags", "keywords", "sortDates", "relations", "links", "referrals"
    })
@Getter
@Setter
public class PageSearch extends AbstractTextSearch implements Predicate<Page> {

    @Valid
    private TextMatcherList broadcasters;

    @Valid
    private TextMatcherList types;

    @Valid
    private TextMatcherList portals;

    @Valid
    private TextMatcherList sections;

    @Valid
    private TextMatcherList genres;

    @Valid
    private ExtendedTextMatcherList tags;

    @Valid
    private ExtendedTextMatcherList keywords;

    private DateRangeMatcherList sortDates;

    @Valid
    private RelationSearchList relations;

    @Valid
    private AssociationSearchList links;

    @Valid
    private AssociationSearchList referrals;

    /**
     * @deprecated For json backwards compatibility
     */
    @JsonSetter
    public void setSortDate(DateRangeMatcherList sortDate) {
        this.sortDates = sortDate;
    }

    @Override
    public boolean hasSearches() {
        return text != null
            || sortDates != null && !sortDates.isEmpty()
            || types != null && !types.isEmpty()
            || broadcasters != null && !broadcasters.isEmpty()
            || portals != null && !portals.isEmpty()
            || sections != null && !sections.isEmpty()
            || genres != null && !genres.isEmpty()
            || tags != null && !tags.isEmpty()
            || keywords != null && !keywords.isEmpty()
            || relations != null && relations.size() > 0
            || links != null && links.size() > 0
            || referrals != null && referrals.size() > 0
            ;
    }

    @Override
    public boolean test(@Nullable Page input) {
        return applyText(input)
            && applyBroadcasters(input)
            && applyPortals(input)
            && applySections(input)
            && applyGenres(input)
            && applyTags(input)
            && applyKeywords(input)
            && applyTypes(input)
            && applySortDates(input)
            && applyRelations(input)
            && applyLinks(input)
            && applyReferrals(input)
            ;
    }

    protected boolean applyText(Page input) {
        if(text == null) {
            return true;
        }
        return Matchers.tokenizedPredicate(text).test(input.getTitle());
    }

    protected boolean applyBroadcasters(Page input) {
        if(broadcasters == null) {
            return true;
        }

        if(input.getBroadcasters() != null) {
            Predicate<String> predicate = Matchers.listPredicate(broadcasters);
            for(Broadcaster b : input.getBroadcasters()) {
                if(predicate.test(b.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean applyTypes(Page input) {
        if(types == null) {
            return true;
        }

        return Matchers.listPredicate(types).test(input.getType().name());
    }

    protected boolean applyPortals(Page input) {
        if(portals == null) {
            return true;
        }

        Portal portal = input.getPortal();
        String name = portal == null ? null : portal.getUrl();
        return Matchers.listPredicate(portals).test(name);
    }

    protected boolean applySections(Page input) {
        if(sections == null) {
            return true;
        }

        Portal portal = input.getPortal();
        Section section = portal == null ? null : portal.getSection();
        String name = section == null ? null : section.getPath();
        return Matchers.listPredicate(sections).test(name);
    }

    protected boolean applyGenres(Page input) {
        if(genres == null) {
            return true;
        }
        if(input.getGenres() != null) {
            Predicate<String> predicate = Matchers.listPredicate(genres);
            for(Genre g : input.getGenres()) {
                if(predicate.test(g.getTermId())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean applyTags(Page input) {
        if (tags == null) {
            return true;
        }
        if (input.getTags() != null) {
            Predicate<String> predicate = Matchers.listPredicate(tags);
            for (String g : input.getTags()) {
                if (predicate.test(g)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean applyKeywords(Page input) {
        if(keywords == null) {
            return true;
        }
        if(input.getKeywords() != null) {
            Predicate<String> predicate = Matchers.listPredicate(keywords);
            for(String g : input.getKeywords()) {
                if(predicate.test(g)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean applySortDates(Page input) {
        if(sortDates == null) {
            return true;
        }
        Instant inputDate = input.getSortDate();
        return inputDate != null && sortDates.test(Date.from(inputDate));
    }

    protected boolean applyRelations(Page input) {
        if (relations == null) {
            return true;
        }

        for (Relation relation : input.getRelations()) {
            if (relations.test(relation)) {
                return true;
            }
        }
        return false;
    }

    protected boolean applyLinks(Page input) {
        if (links == null) {
            return true;
        }

        for (Association association : input.getLinks()) {
            if (links.test(association)) {
                return true;
            }
        }
        return false;
    }

    protected boolean applyReferrals(Page input) {
        if (referrals == null) {
            return true;
        }

        for (Association association : input.getReferrals()) {
            if (referrals.test(association)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "PageSearch{" +
            "broadcasters=" + broadcasters +
            ", types=" + types +
            ", portals=" + portals +
            ", sections=" + sections +
            ", genres=" + genres +
            ", tags=" + tags +
            ", keywords=" + keywords +
            ", sortDates=" + sortDates +
            ", relations=" + relations +
            '}';
    }
}
