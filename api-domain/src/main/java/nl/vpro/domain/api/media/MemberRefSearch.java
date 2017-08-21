/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.AbstractSearch;
import nl.vpro.domain.api.TextMatcherList;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberRefSearchType")
public class MemberRefSearch extends AbstractSearch {

    @Valid
    private TextMatcherList mediaIds;

    @Valid
    private TextMatcherList types;

    public TextMatcherList getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(TextMatcherList mediaIds) {
        this.mediaIds = mediaIds;
    }

    public TextMatcherList getTypes() {
        return types;
    }

    public void setTypes(TextMatcherList types) {
        this.types = types;
    }

    @Override
    public boolean hasSearches() {
        return atLeastOneHasSearches(mediaIds, types);
    }
}
