package nl.vpro.domain.media;

import java.util.Set;

import nl.vpro.jmx.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public interface MediaObjectLockerMXBean {

    @Description("The mids currently locked")
    Set<String> getLocks();

    @Description("The total number of acquired locks")
    int getLockCount();

    @Description("The current number of locks")
    int getCurrentCount();

    @Description("The maximum concurrency level reached since the start of the application")
    int getMaxConcurrency();


}
