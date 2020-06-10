package nl.vpro.domain.media;

import lombok.Lombok;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.checkerframework.checker.nullness.qual.NonNull;
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

    /**
     * The lock the current thread is holding. It would be suspicious (and a possible cause of dead lock) if that is more than one.
     */

    static final ThreadLocal<List<LockHolder<? extends Serializable>>> HOLDS = ThreadLocal.withInitial(ArrayList::new);


    /**
     * Map correlation -> ReentrantLock
     */
    static final Map<MediaIdentifiable.Correlation, LockHolder<MediaIdentifiable.Correlation>>       LOCKED_MEDIA              = new ConcurrentHashMap<>();

    /**
     * Map key -> ReentrantLock
     */
    private static final Map<Serializable, LockHolder<Serializable>> LOCKED_OBJECTS    = new ConcurrentHashMap<>();


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
        /**
         * The argument on which to lock. First one is 0.
         */
        int argNumber() default 0;
        /**
         * A description of the reason of the lock. Only used for logging purposes.
         */
        String reason() default "";

        /**
         * A method on the argument to call to get the MID. Default we suppose the argument to _be_ the MID.e
         */
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


    /**
     * Run in a new transaction, but before that lock the mid.
     *
     * Make sure not to be in a transaction already.
     */
    public static <T> T withMidLock(
        @NonNull TransactionService transactionService,
        String mid,
        @NonNull String reason,
        @NonNull Callable<T> callable) {
        return withMidLock(
            mid,
            reason,
            () -> transactionService.executeInNewTransaction(callable));
    }


    public static <T> T getWithMidLock(
        String mid,
        @NonNull String reason,
        @NonNull Supplier<T> callable) {
        return withMidLock(mid, reason, callable::get);
    }


     public static void withMidLock(
         String mid,
         @NonNull String reason,
         @NonNull Runnable runnable) {
        withMidLock(mid, reason, () -> {
            runnable.run();
            return null;
        });

     }

    public static <T> T withMidLock(
        String mid,
        @NonNull String reason,
        @NonNull Callable<T> callable) {
        return withCorrelationLock(
            MediaIdentifiable.Correlation.mid(mid),
            reason,
            callable);
    }

    public static <T> T withCorrelationLock(
        MediaIdentifiable.Correlation lock,
        @NonNull String reason,
        @NonNull Callable<T> callable) {
        return withObjectLock(lock, reason, callable, LOCKED_MEDIA, (o1, o2) -> o1.getType().equals(o2.getType()));
    }



    public static <T> T withMidsLock(
        Iterable<String> mids,
        @NonNull String reason,
        @NonNull Callable<T> callable) {
        return withObjectsLock(MediaIdentifiable.Correlation.mids(mids), reason, callable, LOCKED_MEDIA, (o1, o2) -> o1.getType().equals(o2.getType()));
    }

     public static void withMidsLock(
         Iterable<String> mid,
         @NonNull String reason,
         @NonNull Runnable runnable) {
         withMidsLock(mid, reason, () -> {
            runnable.run();
            return null;
        });
     }

    public static <T> T withKeyLock(
        Serializable id,
        @NonNull String reason,
        @NonNull Callable<T> callable) {
        return withObjectLock(id, reason, callable, LOCKED_OBJECTS, (o1, o2) -> false);
    }

    @SneakyThrows
    private static <T, K extends Serializable> T withObjectLock(
        K key,
        @NonNull String reason,
        @NonNull Callable<T> callable,
        @NonNull Map<K, LockHolder<K>> locks,
        BiFunction<K, K, Boolean> comparable
        ) {
        if (key == null) {
            //log.warn("Calling with null mid: {}", reason, new Exception());
            log.warn("Calling with null key: {}", reason);
            return callable.call();
        }
        long nanoStart = System.nanoTime();
        LockHolder<K> lock = acquireLock(nanoStart, key, reason, locks, comparable);
        try {
            return callable.call();
        } finally {
            releaseLock(nanoStart, key, reason, locks, lock);
        }
    }

    @SneakyThrows
    private static <T, K extends Serializable> T withObjectsLock(
        @NonNull Iterable<K> keys,
        @NonNull String reason,
        @NonNull Callable<T> callable,
        @NonNull Map<K, LockHolder<K>> locks,
        BiFunction<K, K, Boolean> comparable
        ) {

        final long nanoStart = System.nanoTime();
        final List<LockHolder<K>> lockList = new ArrayList<>();
        final List<K> copyOfKeys = new ArrayList<>();
        for (K key : keys) {
            if (key != null) {
                lockList.add(acquireLock(nanoStart, key, reason, locks, comparable));
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
        @NonNull Iterable<K> keys,
        @NonNull String reason,
        @NonNull Function<Iterable<K>, T> callable,
        @NonNull Map<K, LockHolder<K>> locks,
        BiFunction<K, K, Boolean> comparable) {

        final long nanoStart = System.nanoTime();
        final List<LockHolder<K>> lockList = new ArrayList<>();
        final List<K> copyOfKeys = new ArrayList<>();
        for (K key : keys) {
            if (key != null) {
                Optional<LockHolder<K>> reentrantLock = acquireLock(nanoStart, key, reason, locks, true, comparable);
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

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private static  <K extends Serializable> Optional<LockHolder<K>> acquireLock(
        long nanoStart,
        K key,
        @NonNull String reason,
        final @NonNull Map<K, LockHolder<K>> locks,
        boolean onlyIfFree,
        BiFunction<K, K, Boolean> comparable) {
        LockHolder<K> holder;
        boolean alreadyWaiting = false;
        synchronized (locks) {
            holder = locks.computeIfAbsent(key, (m) -> {
                log.trace("New lock for " + m);
                List<LockHolder<? extends Serializable>> currentLocks = HOLDS.get();
                if (! currentLocks.isEmpty()) {
                    if (MediaObjectLockerAspect.monitor) {
                        if (MediaObjectLockerAspect.sessionFactory != null) {
                            if (MediaObjectLockerAspect.sessionFactory.getCurrentSession().getTransaction().isActive()) {
                                log.warn("Trying to acquire lock in transaction which active already! {}:{} + {}", summarize(), currentLocks, key);
                            }
                        }
                    }
                    if (MediaObjectLockerAspect.stricltyOne && currentLocks.stream()
                        .anyMatch((l) ->
                            key.getClass().isInstance(l.key) && comparable.apply((K) l.key, key)
                        )) {
                        throw new IllegalStateException(String.format("%s Getting a lock on a different key! %s + %s", summarize(), currentLocks.get(0).summarize(), key));
                    } else {
                        log.warn("Getting a lock on a different key! {} + {}", currentLocks, key);
                    }
                }
                LockHolder<K> newHolder = new LockHolder<>(key, reason, new ReentrantLock(), new Exception());
                HOLDS.get().add(newHolder);
                return newHolder;
                }
            );
            if (holder.lock.isLocked() && !holder.lock.isHeldByCurrentThread()) {
                if (onlyIfFree) {
                    return Optional.empty();
                }
                log.debug("There are already threads ({}) for {}, waiting", holder.lock.getQueueLength(), key);
                JMX_INSTANCE.maxConcurrency = Math.max(holder.lock.getQueueLength(), JMX_INSTANCE.maxConcurrency);
                alreadyWaiting = true;
            }

        }

        if (MediaObjectLockerAspect.monitor) {
            long start = System.nanoTime();
            Duration wait = Duration.ofSeconds(5);
            Duration maxWait = Duration.ofSeconds(30);
            try {
                while (!holder.lock.tryLock(wait.toMillis(), TimeUnit.MILLISECONDS)) {
                    Duration duration = Duration.ofNanos(System.nanoTime() - start);
                    log.info("Couldn't  acquire lock for {} during {}, {}, locked by {}", key, duration, summarize(), holder.summarize());
                    if (duration.compareTo(MediaObjectLockerAspect.maxLockAcquireTime) > 0) {
                        log.warn("Took over {} to acquire {}, continuing without lock now", MediaObjectLockerAspect.maxLockAcquireTime, holder);
                        break;
                    }
                    if (wait.compareTo(maxWait) < 0) {
                        wait = wait.multipliedBy(2);
                        if (wait.compareTo(maxWait) > 0) {
                            wait = maxWait;
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
                return Optional.empty();

            }
        } else {
            holder.lock.lock();
        }
        if (alreadyWaiting) {
            log.debug("Released and continuing {}", key);
        }

        JMX_INSTANCE.maxDepth = Math.max(JMX_INSTANCE.maxDepth, holder.lock.getHoldCount());
        log.trace("{} holdcount {}", Thread.currentThread().hashCode(), holder.lock.getHoldCount());
        if (holder.lock.getHoldCount() == 1) {
            JMX_INSTANCE.lockCount.computeIfAbsent(reason, (s) -> new AtomicInteger(0)).incrementAndGet();
            JMX_INSTANCE.currentCount.computeIfAbsent(reason, (s) -> new AtomicInteger()).incrementAndGet();
            Duration aquireTime = Duration.ofNanos(System.nanoTime() - nanoStart);
            log.debug("Acquired lock for {}  ({}) in {}", key, reason, aquireTime);
        }
        return Optional.of(holder);
    }

    private static  <K extends Serializable> LockHolder<K> acquireLock(long nanoStart, K key, @NonNull  String reason, final @NonNull Map<K, LockHolder<K>> locks, BiFunction<K, K, Boolean> comparable) {
        return acquireLock(nanoStart, key, reason, locks, false, comparable).get();
    }


    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private static  <K extends Serializable> void releaseLock(long nanoStart, K key, @NonNull  String reason, final @NonNull Map<K, LockHolder<K>> locks, @NonNull LockHolder<K> lock) {
        synchronized (locks) {
            if (lock.lock.getHoldCount() == 1) {
                if (!lock.lock.hasQueuedThreads()) {
                    log.trace("Removed " + key);
                    LockHolder<K> remove = locks.remove(key);
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

    private static String summarizeStackTrace(Exception ex) {
        return "\n" +
            Stream.of(ex.getStackTrace())
                .filter(e -> e.getClassName().startsWith("nl.vpro"))
                .filter(e -> ! e.getClassName().startsWith(MediaObject.class.getName()))
                .filter(e -> ! e.getClassName().startsWith("nl.vpro.spring"))
                .filter(e -> ! e.getClassName().startsWith("nl.vpro.services"))
                .filter(e -> e.getFileName() != null && ! e.getFileName().contains("generated"))
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n   <-"));
    }

    private static String summarize(Thread t, Exception e) {
        return t.toString() + ":" + summarizeStackTrace(e);

    }
     private static String summarize() {
        return summarize(Thread.currentThread(), new Exception());

    }

    static class LockHolder<K> {
        final K key;
        final ReentrantLock lock;
        final Exception cause;
        final Thread thread;
        final Instant createdAt = Instant.now();
        final String reason;

        private LockHolder(K  k, String reason, ReentrantLock lock, Exception cause) {
            this.key = k;
            this.lock = lock;
            this.cause = cause;
            this.thread = Thread.currentThread();
            this.reason = reason;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LockHolder<K> holder = (LockHolder) o;

            return key.equals(holder.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        /**
         * @since 5.13
         */
        public Duration getAge() {
            return Duration.between(createdAt, Instant.now());
        }

        public String summarize() {
            return key + ":" + createdAt + "(age: " + getAge() + "):" + reason + ":" +
                MediaObjectLocker.summarize(this.thread, this.cause);
        }

        @Override
        public String toString() {
            return "holder:" + key + ":" + createdAt;
        }
    }


}
