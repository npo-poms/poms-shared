/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.*;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.media.*;

/**
 * @author rico
 */
public interface ScheduleService {

    /**
     * @deprecated Use {@link Schedule#ZONE_ID}
     */
    @Deprecated
    ZoneId ZONE_ID = Schedule.ZONE_ID;

    /**
     * @deprecated Use {@link Schedule#START_OF_SCHEDULE}
     */
    @Deprecated
    LocalTime START_OF_SCHEDULE = Schedule.START_OF_SCHEDULE;

    /**
     * @deprecated Moved to {@link Schedule#guideDay()} ()}
     */
    @Deprecated
    static LocalDate today() {
        return Schedule.guideDay();
    }

    /**
     * @param start Inclusive start instant
     * @param stop Exclusive stop  instant
     */
    ScheduleResult list(
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    /**
     * @param start Inclusive start instant
     * @param stop Exclusive stop  instant
     */
    ScheduleResult list(
        Channel channel,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleResult list(
        Net net,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleResult listForBroadcaster(
        String broadcaster,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleResult listForAncestor(
        String mediaId,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleSearchResult find(
        ScheduleForm form,
        Order sort,
        String profile,
        long offset,
        Integer max);


    @Deprecated
    static ZonedDateTime guideDayStart(LocalDate guideDay) {
        return guideDay.atStartOfDay(ZONE_ID).with(START_OF_SCHEDULE);
    }

    @Deprecated
    static ZonedDateTime guideDayStop(LocalDate guideDay) {
        return guideDayStart(guideDay.plusDays(1));
    }
}
