/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "mediaSearchResultItemType")
public class MediaSearchItemResult extends AbstractScoredSearchResult<MediaListItem> {

    public MediaSearchItemResult() {
        super();
    }

    public MediaSearchItemResult(final Long count, final List<ScoredResult<MediaListItem>> result) {
        super(count, result);
    }
}
