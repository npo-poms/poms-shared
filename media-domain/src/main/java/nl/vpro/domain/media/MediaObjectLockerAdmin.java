package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import nl.vpro.jmx.MBeans;
import nl.vpro.util.TimeUtils;
import nl.vpro.util.locker.ObjectLocker;
import nl.vpro.util.locker.ObjectLockerAdmin;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Slf4j
class MediaObjectLockerAdmin implements MediaObjectLockerAdminMXBean {

    private final ObjectLockerAdmin objectLockerAdmin = ObjectLockerAdmin.JMX_INSTANCE;

    @Override
    public Set<String> getLocks() {
        return MediaObjectLocker.LOCKED_MEDIA.values().stream().map(ObjectLocker.LockHolder::summarize).collect(Collectors.toSet());
    }

    @Override
    public int getLockCount() {
        return objectLockerAdmin.getLockCount();
    }

    @Override
    public Map<String, Integer> getLockCounts() {
        return objectLockerAdmin.getLockCounts();
    }

    @Override
    public int getCurrentCount() {
        return objectLockerAdmin.getCurrentCount();
    }

    @Override
    public Map<String, Integer> getCurrentCounts() {
        return objectLockerAdmin.getCurrentCounts();
    }

    @Override
    public int getMaxConcurrency() {
        return objectLockerAdmin.getMaxConcurrency();
    }

    @Override
    public int getMaxDepth() {
        return objectLockerAdmin.getMaxDepth();
    }

    @Override
    public String clearMidLock(String mid, boolean interrupt) {
        ObjectLocker.LockHolder<MediaIdentifiable.Correlation> removed = MediaObjectLocker.LOCKED_MEDIA.remove(MediaIdentifiable.Correlation.mid(mid));
        if (removed != null) {
            removed.disable(interrupt);
            return "Removed " + MediaObjectLocker.LOCKED_MEDIA.remove(MediaIdentifiable.Correlation.mid(mid));
        } else {
            return "No such lock " + mid;
        }
    }

    @Override
    public String lockMid(String mid, String duration) {
        final Duration dur = TimeUtils.parseDuration(duration).orElseThrow(() -> new IllegalArgumentException("Cannot parse " + duration));
        return MBeans.returnString("locking mid " + mid, MBeans.multiLine(log, "locking"), dur.compareTo(Duration.ofSeconds(5)) < 0 ? dur : Duration.ZERO, (logger) -> {
            MediaObjectLocker.withMidLock(mid, "explicit lock for " + duration, new Callable<Void>() {
                @Override
                public Void call() {
                    try {
                        logger.info("Locked " + mid + " for " + dur);
                        Thread.sleep(dur.toMillis());
                    } catch (InterruptedException ie) {
                        logger.info("Interrupted locking of " + mid);
                    }
                    logger.info("Releasing lock for  " + mid + " now");
                    return null;
                }
            });
        });
    }

    @Override
    public String clearMidLocks(boolean interrupt) {
        final int size = MediaObjectLocker.LOCKED_MEDIA.size();
        for (ObjectLocker.LockHolder<MediaIdentifiable.Correlation> value : MediaObjectLocker.LOCKED_MEDIA.values()) {
             value.disable(interrupt);
        }
        return "Removed all mid locks (approx. " + size + ")";
    }

    @Override
    public String getMaxLockAcquireTime() {
        return objectLockerAdmin.getMaxLockAcquireTime();
    }

    @Override
    public void setMaxLockAcquireTime(String duration) {
        objectLockerAdmin.setMaxLockAcquireTime(duration);
    }

    @Override
    public boolean isMonitor() {
        return objectLockerAdmin.isMonitor();
    }

    @Override
    public void setMonitor(boolean monitor) {
        objectLockerAdmin.setMonitor(monitor);
    }
}
