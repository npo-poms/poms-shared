package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Lombok;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import nl.vpro.services.TransactionService;

/**
 * Tool to make sure that the 'authority' related dropboxes don't run at the same time for the same mid.
 *
 * It may be better to (also) introduce more decent hibernate locking (MSE-3751)
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class MediaObjectLocker implements MediaObjectLockerMXBean {

    private static ThreadLocal<Map<String, AtomicInteger>> MIDS = ThreadLocal.withInitial(HashMap::new);


    private static LoadingCache<String, Semaphore> LOCKED_MEDIA = CacheBuilder.newBuilder()
        .build(
            new CacheLoader<String, Semaphore>() {
                @Override
                public Semaphore load(String mid) {
                    return new Semaphore(1);
                }
            }
        );

    private static final MediaObjectLocker instance = new MediaObjectLocker();

    private Map<String, AtomicInteger> lockCount = new HashMap<>();
    private Map<String, AtomicInteger>  currentCount = new HashMap<>();
    @Getter
    private int maxConcurrency = 0;
    @Getter
    private int maxDepth = 0;


    static {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(instance, new ObjectName("nl.vpro.media:name=mediaobjectLocker"));
        } catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }

    @Override
    public Set<String> getLocks() {
        return LOCKED_MEDIA.asMap().keySet();
    }

    @Override
    public int getLockCount() {
        return lockCount.values().stream().mapToInt(AtomicInteger::intValue).sum();

    }

    @Override
    public Map<String, Integer> getLockCounts() {
        return lockCount.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));

    }

    @Override
    public int getCurrentCount() {
        return currentCount.values().stream().mapToInt(AtomicInteger::intValue).sum();
    }

    @Override
    public Map<String, Integer> getCurrentCounts() {
        return currentCount.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));

    }


    /**
     * Adding this annotation of a method with a {@link String} or {@link MediaIdentifiable} object will 'lock' the identifier, and will make sure
     * that not other code doing the same will run simultaneously.
     *
     * Much code like this will be get a mediaobject using this mid, change it and then commit the mediaobject.
     *
     * If another thread is changing the mediaobject in between those event, those changes will be lost.
     *
     * This can therefore be avoided using this annotations (or equivalently by using {@link #runAlone(String, String, Callable)}

     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Mid {
        int argNumber() default 0;
        String reason() default "";
    }

    public static <T> T runAlone(TransactionService transactionService, String mid, String reason, Callable<T> callable) {
        return runAlone(
            mid,
            reason,
            () -> transactionService.executeInNewTransaction(callable));
    }


    public static <T> T getAlone(String mid, String reason, Supplier<T> callable) {
        return runAlone(mid, reason, callable::get);
    }


    @SneakyThrows
    public static <T> T runAlone(String mid, String reason, Callable<T> callable) {
        Long nanoStart = System.nanoTime();
        AtomicInteger integer = MIDS.get().computeIfAbsent(mid, (m) -> new AtomicInteger(0));
        boolean outer = integer.incrementAndGet() == 1;
        instance.maxDepth = Math.max(integer.intValue(), instance.maxDepth);
        try {
            if (outer) {
                instance.lockCount.computeIfAbsent(reason, (s) -> new AtomicInteger(0)).incrementAndGet();
                instance.currentCount.computeIfAbsent(reason, (s) -> new AtomicInteger()).incrementAndGet();
                Semaphore semaphore = get(mid);
                instance.maxConcurrency = Math.max(semaphore.getQueueLength(), instance.maxConcurrency);
                try {
                    if (semaphore.hasQueuedThreads()) {
                        log.info("There are already threads for {}, waiting", mid);
                    }
                    semaphore.acquire();

                    Duration aquireTime = Duration.ofNanos(System.nanoTime() - nanoStart);
                    log.debug("Acquired lock for {}  ({}) in {}", mid, reason, aquireTime);

                    return callable.call();
                } finally {
                    release(semaphore, mid);
                    log.debug("Released lock for {} ({}) in {}", mid, reason, Duration.ofNanos(System.nanoTime() - nanoStart));

                }
            } else {
                return callable.call();
            }
        } finally {
            int get = integer.decrementAndGet();
            if (outer) {
                assert get == 0;
            }
            if (get == 0) {
                instance.currentCount.get(reason).decrementAndGet();
                MIDS.get().remove(mid);
            }
        }
    }




    private static Semaphore get(String mid) {
        if (mid == null) {
            log.warn("Called with mid null!");
            return new Semaphore(1);
        }
        try {
            return LOCKED_MEDIA.get(mid);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    private static void release(Semaphore semaphore, String mid) {
        if (mid != null) {
            LOCKED_MEDIA.invalidate(mid);
        }
        semaphore.release();

    }

}
