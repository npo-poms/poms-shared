package nl.vpro.domain.api.page;

import lombok.*;

import java.time.Instant;
import java.util.function.Function;
import java.util.function.Predicate;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

import nl.vpro.domain.api.*;
import nl.vpro.domain.page.*;
import nl.vpro.domain.user.Broadcaster;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagesSearchType",
    propOrder = {
        // Intellij warnings are incorrect since parent class is @XmlTransient
        "text",
        "broadcasters",
        "types",
        "portals",
        "sections",
        "genres",
        "tags",
        "keywords",
        "sortDates",
        "lastModifiedDates",
        "creationDates",
        "publishDates",
        "relations",
        "links",
        "referrals"
    })
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PageSearch extends AbstractTextSearch<Page> {

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

    @Valid
    private DateRangeMatcherList sortDates;

    @Valid
    private DateRangeMatcherList creationDates;

    @Valid
    private DateRangeMatcherList lastModifiedDates;

    @Valid
    private DateRangeMatcherList publishDates;

    @Valid
    private RelationSearchList relations;

    @Valid
    private AssociationSearchList links;

    @Valid
    private AssociationSearchList referrals;

    @lombok.Builder
    private  PageSearch(@Valid SimpleTextMatcher text, @Valid TextMatcherList broadcasters, @Valid TextMatcherList types, @Valid TextMatcherList portals, @Valid TextMatcherList sections, @Valid TextMatcherList genres, @Valid ExtendedTextMatcherList tags, @Valid ExtendedTextMatcherList keywords, @Valid DateRangeMatcherList sortDates, @Valid DateRangeMatcherList creationDates, @Valid DateRangeMatcherList lastModifiedDates, @Valid DateRangeMatcherList publishDates, @Valid RelationSearchList relations, @Valid AssociationSearchList links, @Valid AssociationSearchList referrals) {
        super(text);
        this.broadcasters = broadcasters;
        this.types = types;
        this.portals = portals;
        this.sections = sections;
        this.genres = genres;
        this.tags = tags;
        this.keywords = keywords;
        this.sortDates = sortDates;
        this.creationDates = creationDates;
        this.lastModifiedDates = lastModifiedDates;
        this.publishDates = publishDates;
        this.relations = relations;
        this.links = links;
        this.referrals = referrals;
    }

    /**
     * @deprecated For json backwards compatibility
     */
    @JsonSetter
    @Deprecated
    public void setSortDate(DateRangeMatcherList sortDate) {
        this.sortDates = sortDate;
    }

    @Override
    public boolean hasSearches() {
        return text != null
            || atLeastOneHasSearches(sortDates, lastModifiedDates, creationDates, publishDates, types, broadcasters, portals, sections, genres, tags, keywords, relations, links, referrals);
    }

    @Override
    public boolean test(@Nullable Page input) {
        return applyText(input) &&
            applyBroadcasters(input) &&
            applyPortals(input) &&
            applySections(input) &&
            applyGenres(input) &&
            applyTags(input) &&
            applyKeywords(input) &&
            applyTypes(input) &&
            applySortDates(input) &&
            applyLastModifiedDates(input) &&
            applyCreationDates(input) &&
            applyPublishDates(input) &&
            applyRelations(input) &&
            applyLinks(input) &&
            applyReferrals(input)
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
            Predicate<String> predicate = Matchers.listPredicate(broadcasters.getMatchers());
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
        return applyDateRange(input, sortDates, Page::getSortDate);
    }

    protected boolean applyLastModifiedDates(Page input) {
        return applyDateRange(input, lastModifiedDates, Page::getLastModified);
    }

    protected boolean applyCreationDates(Page input) {
        return applyDateRange(input, creationDates, Page::getCreationDate);
    }

    protected boolean applyPublishDates(Page input) {
        return applyDateRange(input, publishDates, Page::getLastPublished);
    }

    protected boolean applyDateRange(Page input, DateRangeMatcherList range, Function<Page, Instant> inputDateGetter) {
        if (range == null) {
            return true;
        }
        Instant inputDate = inputDateGetter.apply(input);
        return inputDate != null && range.test(inputDate);
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

    public static class Builder {
        public Builder simpleText(String search) {
            return text(new SimpleTextMatcher(search));
        }

        public Builder semanticText(String search) {
            SimpleTextMatcher simpleTextMatcher = new SimpleTextMatcher(search);
            simpleTextMatcher.setSemantic(true);
            return text(simpleTextMatcher);
        }
    }
}
