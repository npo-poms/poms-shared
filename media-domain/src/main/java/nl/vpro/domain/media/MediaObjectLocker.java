package nl.vpro.domain.media;

import lombok.Lombok;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.event.Level;

import nl.vpro.logging.Slf4jHelper;
import nl.vpro.services.TransactionService;

/**
 * Tool to make sure that the 'authority' related dropboxes and other services don't run at the same time for the same mid.
 *
 * It may be better to (also) introduce more decent hibernate locking (MSE-3751)
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class MediaObjectLocker {

    static final ThreadLocal<List<Holder>>      HOLDS = ThreadLocal.withInitial(ArrayList::new);


    /**
     * Map mid -> ReentrantLock
     */
    static final Map<String, Holder>       LOCKED_MEDIA             = new ConcurrentHashMap<>();

    /**
     * Map key -> ReentrantLock
     */
    private static final Map<Serializable, Holder> LOCKED_OBJECTS = new ConcurrentHashMap<>();


    private static final MediaObjectLockerAdmin          JMX_INSTANCE    = new MediaObjectLockerAdmin();



    static {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(JMX_INSTANCE, new ObjectName("nl.vpro.media:name=objectLocker"));
        } catch (Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }


    /**
     * Adding this annotation of a method with a {@link String} or {@link MediaIdentifiable} object will 'lock' the identifier, and will make sure
     * that no other code doing the same will run simultaneously.
     *
     * Much code like this will be get a mediaobject using this mid, change it and then commit the mediaobject.
     *
     * If another thread is changing the mediaobject in between those event, those changes will be lost.
     *
     * This can therefore be avoided using this annotations (or equivalently by using {@link #withMidLock(String, String, Callable)}

     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Mid {
        int argNumber() default 0;
        String reason() default "";
        String method() default "";
    }

    /**
     * Like {@link Mid}, but now for nl.vpro.domain.subtitles.SubtitlesId.
     */

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Sid {
        int argNumber() default 0;
        String reason() default "";
    }



    public static <T> T withMidLock(
        @Nonnull TransactionService transactionService,
        String mid,
        @Nonnull String reason,
        @Nonnull Callable<T> callable) {
        return withMidLock(
            mid,
            reason,
            () -> transactionService.executeInNewTransaction(callable));
    }


    public static <T> T getWithMidLock(
        String mid,
        @Nonnull String reason,
        @Nonnull Supplier<T> callable) {
        return withMidLock(mid, reason, callable::get);
    }


     public static void withMidLock(
         String mid,
         @Nonnull String reason,
         @Nonnull Runnable runnable) {
        withMidLock(mid, reason, () -> {
            runnable.run();
            return null;
        });

     }

    public static <T> T withMidLock(
        String mid,
        @Nonnull String reason,
        @Nonnull Callable<T> callable) {
        return withObjectLock(mid, reason, callable, LOCKED_MEDIA);
    }


    public static <T> T withMidsLock(
        Iterable<String> mids,
        @Nonnull String reason,
        @Nonnull Callable<T> callable) {
        return withObjectsLock(mids, reason, callable, LOCKED_MEDIA);
    }

     public static void withMidsLock(
         Iterable<String> mid,
         @Nonnull String reason,
         @Nonnull Runnable runnable) {
         withMidsLock(mid, reason, () -> {
            runnable.run();
            return null;
        });
     }


     public static void withMidsLockIfFree(
         Iterable<String> mid,
         @Nonnull String reason,
         @Nonnull Consumer<Iterable<String>> consumer) {
        withObjectsLockIfFree(mid, reason, (mids) -> {consumer.accept(mids); return null; }, LOCKED_MEDIA);

     }


    public static <T> T withKeyLock(
        Serializable id,
        @Nonnull String reason,
        @Nonnull Callable<T> callable) {
        return withObjectLock(id, reason, callable, LOCKED_OBJECTS);
    }

    @SneakyThrows
    private static <T, K extends Serializable> T withObjectLock(
        K key,
        @Nonnull String reason,
        @Nonnull Callable<T> callable,
        @Nonnull Map<K, Holder> locks) {
        if (key == null) {
            //log.warn("Calling with null mid: {}", reason, new Exception());
            log.warn("Calling with null key: {}", reason);
            return callable.call();
        }
        long nanoStart = System.nanoTime();
        Holder lock = aquireLock(nanoStart, key, reason, locks);
        try {
            return callable.call();
        } finally {
            releaseLock(nanoStart, key, reason, locks, lock);
        }
    }

    @SneakyThrows
    private static <T, K extends Serializable> T withObjectsLock(
        @Nonnull Iterable<K> keys,
        @Nonnull String reason,
        @Nonnull Callable<T> callable,
        @Nonnull Map<K, Holder> locks) {

        final long nanoStart = System.nanoTime();
        final List<Holder> lockList = new ArrayList<>();
        final List<K> copyOfKeys = new ArrayList<>();
        for (K key : keys) {
            if (key != null) {
                lockList.add(aquireLock(nanoStart, key, reason, locks));
                copyOfKeys.add(key);
            }
        }
        try {
            return callable.call();
        } finally {
            int i = 0;
            for (K key : copyOfKeys) {
                releaseLock(nanoStart, key, reason, locks, lockList.get(i++));
            }
        }
    }
    @SneakyThrows
    private static <T, K extends Serializable> T withObjectsLockIfFree(
        @Nonnull Iterable<K> keys,
        @Nonnull String reason,
        @Nonnull Function<Iterable<K>, T> callable,
        @Nonnull Map<K, Holder> locks) {

        final long nanoStart = System.nanoTime();
        final List<Holder> lockList = new ArrayList<>();
        final List<K> copyOfKeys = new ArrayList<>();
        for (K key : keys) {
            if (key != null) {
                Optional<Holder> reentrantLock = aquireLock(nanoStart, key, reason, locks, true);
                if (reentrantLock.isPresent()) {
                    lockList.add(reentrantLock.get());
                    copyOfKeys.add(key);
                }
            }
        }
        try {
            return callable.apply(copyOfKeys);
        } finally {
            int i = 0;
            for (K key : copyOfKeys) {
                releaseLock(nanoStart, key, reason, locks, lockList.get(i++));
            }
        }
    }

    private static  <K extends Serializable> Optional<Holder> aquireLock(long nanoStart, K key, @Nonnull  String reason, final @Nonnull Map<K, Holder> locks, boolean onlyIfFree) {
        Holder lock;
        boolean alreadyWaiting = false;
        synchronized (locks) {
            lock = locks.computeIfAbsent(key, (m) -> {
                log.trace("New lock for " + m);
                if (! HOLDS.get().isEmpty()) {
                    log.warn("Getting a lock on a different key! {} + {}", HOLDS.get(), key);
                }


                Holder holder = new Holder(key, new ReentrantLock(), new Exception());
                HOLDS.get().add(holder);
                return holder;
                }
            );
            if (lock.lock.isLocked() && !lock.lock.isHeldByCurrentThread()) {
                if (onlyIfFree) {
                    return Optional.empty();
                }
                log.debug("There are already threads ({}) for {}, waiting", lock.lock.getQueueLength(), key);
                JMX_INSTANCE.maxConcurrency = Math.max(lock.lock.getQueueLength(), JMX_INSTANCE.maxConcurrency);
                alreadyWaiting = true;
            }

        }

        long start = System.nanoTime();
        try {
            while (!lock.lock.tryLock(5, TimeUnit.SECONDS)) {
                log.info("Couldn't  acquire lock for {} during {}, {}, locked by {}", key, Duration.ofNanos(System.nanoTime() - start), ExceptionUtils.getStackTrace(new Exception()), ExceptionUtils.getStackTrace(lock.cause));
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();

        }
        if (alreadyWaiting) {
            log.debug("Released and continuing {}", key);
        }

        JMX_INSTANCE.maxDepth = Math.max(JMX_INSTANCE.maxDepth, lock.lock.getHoldCount());
        log.trace("{} holdcount {}", Thread.currentThread().hashCode(), lock.lock.getHoldCount());
        if (lock.lock.getHoldCount() == 1) {
            JMX_INSTANCE.lockCount.computeIfAbsent(reason, (s) -> new AtomicInteger(0)).incrementAndGet();
            JMX_INSTANCE.currentCount.computeIfAbsent(reason, (s) -> new AtomicInteger()).incrementAndGet();
            Duration aquireTime = Duration.ofNanos(System.nanoTime() - nanoStart);
            log.debug("Acquired lock for {}  ({}) in {}", key, reason, aquireTime);
        }
        return Optional.of(lock);
    }

    private static  <K extends Serializable> Holder aquireLock(long nanoStart, K key, @Nonnull  String reason, final @Nonnull Map<K, Holder> locks) {
        return aquireLock(nanoStart, key, reason, locks, false).get();
    }


    private static  <K extends Serializable> void releaseLock(long nanoStart, K key, @Nonnull  String reason,  final @Nonnull Map<K, Holder> locks, @Nonnull Holder lock) {
        synchronized (locks) {
            if (lock.lock.getHoldCount() == 1) {
                if (!lock.lock.hasQueuedThreads()) {
                    log.trace("Removed " + key);
                    Holder remove = locks.remove(key);
                    if (remove != null) {


                    }
                }
                JMX_INSTANCE.currentCount.computeIfAbsent(reason, (s) -> new AtomicInteger()).decrementAndGet();
                Duration duration = Duration.ofNanos(System.nanoTime() - nanoStart);
                Slf4jHelper.log(log, duration.compareTo(Duration.ofSeconds(30))> 0 ? Level.WARN :  Level.DEBUG,
                    "Released lock for {} ({}) in {}", key, reason, Duration.ofNanos(System.nanoTime() - nanoStart));
            }
            HOLDS.get().remove(lock);
            lock.lock.unlock();
            locks.notifyAll();
        }
    }

    private static class Holder {
        final Object key;
        final ReentrantLock lock;
        final Exception cause;

        private Holder(Object k, ReentrantLock lock, Exception cause) {
            this.key = k;
            this.lock = lock;
            this.cause = cause;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Holder holder = (Holder) o;

            return key.equals(holder.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public String toString() {
            return "holder:" + key;
        }
    }


}
