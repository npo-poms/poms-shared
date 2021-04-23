/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.*;
import java.time.temporal.ChronoUnit;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Net;

/**
 * @author rico
 */
public interface ScheduleService {

    ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");
    LocalTime START_OF_SCHEDULE = LocalTime.of(6, 0);

    static LocalDate today() {
        ZonedDateTime now = ZonedDateTime.now(ZONE_ID);
        if (now.toLocalTime().isBefore(START_OF_SCHEDULE)) {
            return LocalDate.now(ZONE_ID).minus(1, ChronoUnit.DAYS);
        } else {
            return LocalDate.now(ZONE_ID);
        }
    }

    ScheduleResult list(
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

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


    static ZonedDateTime guideDayStart(LocalDate guideDay) {
        return guideDay.atStartOfDay(ZONE_ID).with(START_OF_SCHEDULE);
    }

    static ZonedDateTime guideDayStop(LocalDate guideDay) {
        return guideDayStart(guideDay.plusDays(1));
    }
}
