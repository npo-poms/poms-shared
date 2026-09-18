/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.api.*;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaRelationSearchType",
    propOrder = {
        "types",
        "broadcasters",
        "values",
        "uriRefs"
    }
)
@NoArgsConstructor
public class RelationSearch extends AbstractRelationSearch {

    @lombok.Builder
    protected RelationSearch(
        TextMatcherList types,
        TextMatcherList broadcasters,
        ExtendedTextMatcherList values,
        TextMatcherList uriRefs) {
        super(types, broadcasters, values, uriRefs);
    }
}
