package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.jmx.MBeans;
import nl.vpro.services.TransactionService;
import nl.vpro.util.locker.ObjectLocker;
import nl.vpro.util.locker.ObjectLocker.LockHolder;

/**
 * Tool to make sure that the 'authority' related dropboxes and other services don't run at the same time for the same mid.
 *
 * It may be better to (also) introduce more decent hibernate locking (MSE-3751)
 *
 * This basicly wraps {@link ObjectLocker}, but keeps a separate map of locked objects, dedicated to media identifiables
 *
 * Also, it defines some annotations (for use with {@link MediaObjectLockerAspect} (to facilitate locking via annotation), and some utility methods.
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class MediaObjectLocker {

    /**
     * Map correlation -> ReentrantLock
     */
    static final Map<MediaIdentifiable.Correlation, LockHolder<MediaIdentifiable.Correlation>>       LOCKED_MEDIA              = new ConcurrentHashMap<>();


    private static final MediaObjectLockerAdmin JMX_INSTANCE    = new MediaObjectLockerAdmin();


     static {
        try {
            MBeans.registerBean(new ObjectName("nl.vpro.media:name=mediaObjectLocker"), JMX_INSTANCE);
        } catch (MalformedObjectNameException ignored) {
            // cannot happen
        }
    }


    /**
     * Adding this annotation of a method with a {@link String} or {@link MediaIdentifiable} argument will 'lock' the identifier, and will make sure
     * that no other code doing the same will run simultaneously.
     *
     * Much code like this will be get a mediaobject using this mid, change it and then commit the mediaobject.
     *
     * If another thread is changing the mediaobject in between those events, those changes will be lost.
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
     * Like {@link Mid}, but now for {@code nl.vpro.domain.subtitles.SubtitlesId}
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
    public static <T> T executeInNewTransactionWithMidLock(
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
        return ObjectLocker.withObjectLock(lock, reason, callable, LOCKED_MEDIA, (
            o1, o2) ->
            o1 instanceof MediaIdentifiable.Correlation && Objects.equals(((MediaIdentifiable.Correlation) o1).getType(), o2.getType()));
    }

}
