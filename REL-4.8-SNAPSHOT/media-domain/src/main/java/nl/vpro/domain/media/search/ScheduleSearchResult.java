/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.Xmlns;

@XmlType(name = "scheduleSearchResultType", propOrder = {})
public class ScheduleSearchResult extends SearchResult<Schedule> {

    public ScheduleSearchResult() {
        super();
    }

    public ScheduleSearchResult(final Integer count, final List<Schedule> result) {
        super(count, result);
    }
}
