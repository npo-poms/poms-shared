/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.v3.schedule;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import nl.vpro.domain.api.media.ScheduleResult;


public interface ScheduleRestServiceWithDefaults extends ScheduleRestService {

    @Deprecated
    default ScheduleResult list(Date guideDay, Date start, Date stop, String properties, String sort, Long offset, Integer max) {
        return list(toLocalDate(guideDay), toInstant(start), toInstant(stop), properties, sort, offset, max);
    }


    @Deprecated
    default ScheduleResult listForAncestor(String mediaId, Date guideDay, Date start, Date stop, String properties, String sort, Long offset, Integer max) {
        return listForAncestor(mediaId, toLocalDate(guideDay), toInstant(start), toInstant(stop), properties, sort, offset, max);
    }

    @Deprecated
    default ScheduleResult listBroadcaster(String broadcaster, Date guideDay, Date start, Date stop, String properties, String sort, Long offset, Integer max) {
        return listBroadcaster(broadcaster, toLocalDate(guideDay), toInstant(start), toInstant(stop), properties, sort, offset, max);
    }

    @Deprecated
    default ScheduleResult listChannel(String channel, Date guideDay, Date start, Date stop, String properties, String sort, Long offset, Integer max) {
        return listChannel(channel, toLocalDate(guideDay), toInstant(start), toInstant(stop), properties, sort, offset, max);
    }

    @Deprecated
    default ScheduleResult listNet(String net, Date guideDay, Date start, Date stop, String properties, String sort, Long offset, Integer max) {
        return listNet(net, toLocalDate(guideDay), toInstant(start), toInstant(stop), properties, sort, offset, max);
    }


    static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atOffset(ZoneOffset.UTC).toLocalDate();
    }


    static Instant toInstant(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atOffset(ZoneOffset.UTC).toInstant();
    }
}
