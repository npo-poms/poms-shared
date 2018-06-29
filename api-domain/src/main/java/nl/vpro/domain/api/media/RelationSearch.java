/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.AbstractRelationSearch;
import nl.vpro.domain.api.ExtendedTextMatcherList;
import nl.vpro.domain.api.TextMatcherList;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaRelationSearchType",
    propOrder = {"types", "broadcasters", "values", "uriRefs"})
@NoArgsConstructor
public class RelationSearch extends AbstractRelationSearch {

    @lombok.Builder
    protected RelationSearch(
        @Valid TextMatcherList types,
        @Valid TextMatcherList broadcasters,
        @Valid ExtendedTextMatcherList values,
        @Valid TextMatcherList uriRefs) {
        super(types, broadcasters, values, uriRefs);
    }
}
