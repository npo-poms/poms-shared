/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "mediaSearchResultItemType")
public class MediaSearchItemResult extends SearchResult<MediaListItem> {

    public MediaSearchItemResult() {
        super();
    }

    public MediaSearchItemResult(final Integer count, final List<MediaListItem> result) {
        super(count, result);
    }
}
