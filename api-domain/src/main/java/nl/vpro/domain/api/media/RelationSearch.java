/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

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
        @Valid TextMatcherList types,
        @Valid TextMatcherList broadcasters,
        @Valid ExtendedTextMatcherList values,
        @Valid TextMatcherList uriRefs) {
        super(types, broadcasters, values, uriRefs);
    }
}
