/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.List;

import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "mediaSearchResultItemType")
public class MediaSearchItemResult extends AbstractScoredSearchResult<MediaListItem> {

    public MediaSearchItemResult() {
        super();
    }

    public MediaSearchItemResult(final Long count, final List<ScoredResult<MediaListItem>> result) {
        super(count, result);
    }
}
