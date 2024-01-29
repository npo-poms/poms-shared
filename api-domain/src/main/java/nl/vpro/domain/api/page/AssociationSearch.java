package nl.vpro.domain.api.page;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.api.*;
import nl.vpro.domain.page.Association;
import nl.vpro.domain.page.LinkType;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
@XmlType(name = "associationSearchType")
@XmlAccessorType(XmlAccessType.FIELD)
public class AssociationSearch extends AbstractSearch<Association>  {

    public static AssociationSearch of(LinkType type) {
        return of(type, Match.MUST);
    }

    public static AssociationSearch of(LinkType type, Match match) {
        AssociationSearch search = new AssociationSearch();
        search.setMatch(match);
        search.urls = null;
        search.types = TextMatcherList.must(TextMatcher.must(type.name(), StandardMatchType.TEXT));
        return search;
    }


    @Valid
    private TextMatcherList urls;

    @Valid
    private TextMatcherList types;


    public TextMatcherList getUrls() {
        return urls;
    }

    public void setUrls(TextMatcherList url) {
        this.urls = urls;
    }

    public TextMatcherList getTypes() {
        return types;
    }

    public void setTypes(TextMatcherList type) {
        this.types = type;
    }

    @Override
    public boolean test(Association association) {
        if (association == null) {
            return  false;
        }
        if (urls != null) {
            if (!Matchers.listPredicate(urls).test(association.getPageRef())) {
                return false;
            }
        }
        if (types != null) {
            LinkType aType = association.getType();
            return Matchers.listPredicate(types).test(aType == null ? null : aType.name());
        }
        return false;

    }

    @Override
    public boolean hasSearches() {
        return atLeastOneHasSearches(types, urls);
    }
}
