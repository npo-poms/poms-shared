/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.*;
import java.util.List;

import nl.vpro.domain.media.Schedule;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "schedules")
public class ScheduleSearchList extends TransferList<ScheduleSearchView>{

    private ScheduleSearchList() {
    }

    public static ScheduleSearchList create(List<Schedule> fullList) {
        return create(fullList, fullList.size());
    }

    public static ScheduleSearchList create(List<Schedule> fullList, Integer results) {
        ScheduleSearchList simpleList = new ScheduleSearchList();

        for (Schedule schedule : fullList) {
            simpleList.list.add(ScheduleSearchView.create(schedule));
        }

        simpleList.success = true;
        simpleList.results = results;
        return simpleList;
    }
}