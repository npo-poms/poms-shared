/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.ScheduleEvent;

@XmlType(name = "scheduleSearchResultType", propOrder = {})
public class ScheduleSearchResult extends AbstractSearchResult<ScheduleEvent> {

    public ScheduleSearchResult() {
        super();
    }

    public ScheduleSearchResult(final Long count, final List<ScheduleEvent> result) {
        super(count, result);
    }
}
