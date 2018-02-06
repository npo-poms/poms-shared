/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "termSearchType")
public class TermSearch extends AbstractSearch {

    @Valid
    private TextMatcherList ids;

    public TermSearch() {
    }

    public TermSearch(TextMatcherList ids) {
        this.ids = ids;
    }


    @lombok.Builder
    private TermSearch(Match match, TextMatcherList ids) {
        super(match);
        this.ids = ids;
    }

    public TextMatcherList getIds() {
        return ids;
    }

    public void setIds(TextMatcherList ids) {
        this.ids = ids;
    }

    @Override
    public boolean hasSearches() {
        return atLeastOneHasSearches(ids);
    }
}
