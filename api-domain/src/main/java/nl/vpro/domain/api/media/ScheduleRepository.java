package nl.vpro.domain.api.media;

import java.time.Instant;
import java.time.LocalDate;

import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.Net;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public interface ScheduleRepository  {

    ScheduleResult listSchedules(
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleResult listSchedules(
        Channel channel,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleResult listSchedules(
        Channel channel,
        LocalDate guideDay,
        Order order,
        long offset,
        Integer max);

    ScheduleResult listSchedules(
        Net net,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleResult listSchedulesForBroadcaster(
        String broadcaster,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleResult listSchedulesForAncestor(
        String mediaId,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleResult listSchedulesForMediaType(
        MediaType mediaType,
        Instant start,
        Instant stop,
        Order order,
        long offset,
        Integer max);

    ScheduleSearchResult findSchedules(
        ProfileDefinition<MediaObject> profile, ScheduleForm form,
        Order sort,
        long offset,
        Integer max);
}
