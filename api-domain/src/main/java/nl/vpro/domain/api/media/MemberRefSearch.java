/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.AbstractSearch;
import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.TextMatcherList;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberRefSearchType")
public class MemberRefSearch extends AbstractSearch<MediaObject> {

    @Valid
    @Getter
    @Setter
    private TextMatcherList mediaIds;

    @Valid
    @Getter
    @Setter
    private TextMatcherList types;

    public MemberRefSearch() {

    }
    @lombok.Builder
    private MemberRefSearch(Match match, TextMatcherList mediaIds, TextMatcherList types) {
        super(match);
        this.mediaIds = mediaIds;
        this.types = types;

    }

    @Override
    public boolean hasSearches() {
        return atLeastOneHasSearches(mediaIds, types);
    }

    @Override
    public boolean test(MediaObject s) {
        return false;

    }
}
