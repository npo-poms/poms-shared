package nl.vpro.domain.media;

import java.util.Map;
import java.util.Set;

import nl.vpro.jmx.Description;
import nl.vpro.jmx.Name;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public interface MediaObjectLockerAdminMXBean   {

    @Description("The mids currently locked")
    Set<String> getLocks();

    @Description("The total number of acquired locks. So this grows continuously.")
    int getLockCount();

    @Description("The total number of acquired locks per 'reason'. So this grows continuously.")
    Map<String, Integer> getLockCounts();

    @Description("The current number of locks. Should be a low number, most of the time zero.")
    int getCurrentCount();

    @Description("The total number of acquired locks per reason. So this grows continuously.")
    Map<String, Integer> getCurrentCounts();

    @Description("The maximum concurrency level reached since the start of the application. I.e. the number of threads trying to acces the same lock simultaneously")
    int getMaxConcurrency();


    @Description("The maximum depth reach. I.e. the maximum number of 'nested' code locking the same mid.")
    int getMaxDepth();

    @Description("Explicitely clear a lock on some mid")
    String clearMidLock(@Name("mid") String mid);

    @Description("Explicitely lock a mid for a certain period. This simulates long processes, and can be used to assess such a situation")
    String lockMid(@Description("The mid to lock") @Name("mid") String mid, @Description("How long") @Name("duration") String duration);

    String clearMidLocks();

    String getMaxLockAcquireTime();

    void setMaxLockAcquireTime(String duration);


    boolean isMonitor();

    void setMonitor(boolean monitor);






}
