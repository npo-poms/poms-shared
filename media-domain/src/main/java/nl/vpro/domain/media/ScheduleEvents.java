/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Utilities related to {@link ScheduleEvent}
 *
 * @author Roelof Jan Koekoek
 * @since 1.7
 */
public class ScheduleEvents {

    private ScheduleEvents() {
    }

    /**
     * Equals to events on their real start time. Returns true when:
     * <p/>
     * channel = channel AND (start + offset) = (start + offset)
     */
    public static boolean equalHonoringOffset(ScheduleEvent event1, ScheduleEvent event2) {
        return
            event1.getChannel() != null && event1.getChannel().equals(event2.getChannel()) &&
                event1.getRealStartInstant() != null && event2.getRealStartInstant() != null && event1.getRealStartInstant().toEpochMilli() == event2.getRealStartInstant().toEpochMilli();
    }


    public static String userFriendlyToString(Iterable<ScheduleEvent> scheduleEvents) {
        StringBuilder builder = new StringBuilder();
        for (ScheduleEvent event : scheduleEvents) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append(event.getChannel()).append(", ").append(event.getStartInstant().toString());

        }
        return builder.toString();
    }

    public static String userFriendlyToString(Program object) {
        return userFriendlyToString(object.getScheduleEvents());
    }


    public static boolean isRerun(ScheduleEvent scheduleEevent) {
        return Repeat.isRerun(scheduleEevent.getRepeat());
    }
    public static boolean isOriginal(ScheduleEvent scheduleEevent) {
        return Repeat.isOriginal(scheduleEevent.getRepeat());
    }

    public static String getRerunText(ScheduleEvent scheduleEvent) {
        return scheduleEvent.repeat == null ? null : scheduleEvent.repeat.getValue();
    }

    public static Optional<ScheduleEvent> sortDateEventForProgram(Iterable<ScheduleEvent> scheduleEvents) {
        ScheduleEvent result = null;
        if (scheduleEvents != null) {
            for (ScheduleEvent s : scheduleEvents) {
                if (ScheduleEvents.isOriginal(s) && (result == null || s.getStartInstant().isAfter(result.getStartInstant()))) {
                    result = s;
                }
            }
        }
        return Optional.ofNullable(result);
    }

    public static Optional<ScheduleEvent> getFirstScheduleEvent(Iterable<ScheduleEvent> scheduleEvents, boolean ignoreReruns) {
        if (scheduleEvents != null) {
            for (ScheduleEvent s : scheduleEvents) {
                if (! ignoreReruns || ScheduleEvents.isOriginal(s)) {
                    return Optional.of(s);
                }
            }
        }
        return Optional.empty();

    }

    public static Optional<ScheduleEvent> getLastScheduleEvent(Iterable<ScheduleEvent> scheduleEvents, boolean ignoreReruns) {
        ScheduleEvent result = null;
        if (scheduleEvents != null) {
            for (ScheduleEvent s : scheduleEvents) {
                if (!ignoreReruns || ScheduleEvents.isOriginal(s)) {
                    result = s;
                }
            }
        }
        return Optional.ofNullable(result);
    }


    /**
     * Finds in the current schedule the event with the same channel and start instant.
     */
     public static Optional<ScheduleEvent> findScheduleEventWithCompareTo(final Iterable<ScheduleEvent> scheduleEvents, ScheduleEvent event) {
        for (ScheduleEvent e : scheduleEvents) {
            if (e.compareTo(event) == 0) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }


    /**
     * Finds in the current schedule the event with the same channel and start instant.
     */
     public static  List<ScheduleEvent> findScheduleEventsCloseTo(final Iterable<ScheduleEvent> scheduleEvents, ScheduleEvent event, Duration margin) {
         BiFunction<ScheduleEvent, ScheduleEvent, Duration> distance =
             (e1, e2) -> Duration.between(e1.getRealStartInstant(), e2.getRealStartInstant()).abs();


         List<ScheduleEvent> result = new ArrayList<>();
         for (ScheduleEvent e : scheduleEvents) {
             if (!Objects.equals(e.getChannel(), event.getChannel())) {
                 continue;
             }
             if (distance.apply(e, event).compareTo(margin) < 0) {
                 result.add(e);
             }
         }
         result.sort(Comparator.comparing(o -> distance.apply(event, o)));
         return result;
    }

}
