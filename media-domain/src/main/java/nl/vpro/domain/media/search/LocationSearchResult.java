/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.List;

import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Location;

@XmlType(name = "locationSearchResultType")
public class LocationSearchResult extends AbstractSearchResult<Location> {

    public LocationSearchResult() {
        super();
    }
    public LocationSearchResult(final Long count, final List<Location> result) {
        super(count, result);
    }

}
