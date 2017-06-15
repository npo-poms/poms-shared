/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.web.media;

import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.search.ScheduleForm;
import nl.vpro.domain.media.search.ScheduleSearchResult;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.media.ScheduleList;
import nl.vpro.transfer.extjs.media.ScheduleSearchList;

@Service("extScheduleService")
@Transactional
public class ExtScheduleServiceImpl implements ExtScheduleService {

    @Autowired
    MediaService mediaService;

    @Autowired
    ScheduleEventService scheduleEventService;

    @Autowired
    PermissionEvaluator permissionEvaluator;


    @Override
    public TransferList<?> search(ScheduleForm form) {
        ScheduleSearchResult sr = scheduleEventService.findSchedules(form);
        return ScheduleSearchList.create(sr.getResult(), sr.getCount());
    }

    @Override
    public TransferList<?> getSchedule(Channel channel, Date day) {
        Schedule schedule = scheduleEventService.findSchedule(channel, day);
        if(schedule == null) {
            schedule = new Schedule(channel, day, Collections.emptyList());
        }
        return ScheduleList.create(schedule);
    }
}
