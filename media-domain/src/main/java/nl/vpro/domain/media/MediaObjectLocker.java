package nl.vpro.domain.media;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import nl.vpro.services.TransactionService;

/**
 * Tool to make sure that the 'authority' related dropboxes don't run at the same time for the same mid.
 *
 * It may be better to introduce more decent hibernate locking (MSE-3751), in which case this class can be removed again.
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class MediaObjectLocker {

    private static ThreadLocal<Map<String, AtomicInteger>> mids = ThreadLocal.withInitial(HashMap::new);

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Mid {
        int argNumber() default 0;
        String reason() default "midlock annotation";
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
        AtomicInteger integer = mids.get().computeIfPresent(mid, (mi, a) -> { return  new AtomicInteger(0); });
        try {
            if (integer.incrementAndGet() == 1) {
                Semaphore semaphore = get(mid);
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
            if (get == 0) {
                mids.get().remove(mid);
            }
        }
    }



    private static LoadingCache<String, Semaphore> LOCKED_MEDIA = CacheBuilder.newBuilder()
        .build(
            new CacheLoader<String, Semaphore>() {
                @Override
                public Semaphore load(String mid) {
                    return new Semaphore(1);
                }
            }
        );

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
