package nl.vpro.domain.media;

import lombok.Lombok;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.event.Level;

import nl.vpro.logging.Slf4jHelper;
import nl.vpro.util.locker.ObjectLocker;


/**
 * This makes locking on mid easier.
 * <p>
 * Just annotate your method with {@link MediaObjectLocker.Mid} and it should automatically lock the mid if it isn't yet.
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Aspect
@Slf4j
// DeclarePrecedence not supported by spring AOP, this is just extended in media project with a spring @Order annotation instead.
//@DeclarePrecedence("nl.vpro.domain.media.MediaObjectLockerAspect, org.springframework.transaction.aspectj.AnnotationTransactionAspect, *")
public abstract class MediaObjectLockerAspect  {


    abstract Optional<String> getCorrelationId(String mid);


    @Around(value="@annotation(annotation)", argNames="joinPoint,annotation")
    public Object lockMid(ProceedingJoinPoint joinPoint, MediaObjectLocker.Mid annotation) {
        final Object media = joinPoint.getArgs()[annotation.argNumber()];
        final String method = annotation.method();
        final MediaIdentifiable.Correlation correlation = getCorrelation(method, media);

        String reason = annotation.reason();

        if (StringUtils.isEmpty(reason)) {
            reason = joinPoint.getSignature().getDeclaringType().getSimpleName() + "#" + joinPoint.getSignature().getName();
        }

        return MediaObjectLocker.withCorrelationLock(correlation, reason, () -> {
            try {
                return joinPoint.proceed(joinPoint.getArgs());
            } catch(Throwable t) {
                throw Lombok.sneakyThrow(t);
            }

        });
    }


    @Around(value="@annotation(annotation)", argNames="joinPoint,annotation")
    public Object lockSid(ProceedingJoinPoint joinPoint, MediaObjectLocker.Sid annotation) {
        final Object scheduleEvent = joinPoint.getArgs()[annotation.argNumber()];
        final ScheduleEventIdentifier sid = getSid(scheduleEvent);
        String reason = annotation.reason();
        if (StringUtils.isEmpty(reason)) {
            reason = joinPoint.getSignature().getDeclaringType().getSimpleName() + "#" + joinPoint.getSignature().getName();
        }

        return ObjectLocker.withKeyLock(sid, reason, () -> {
            try {
                return joinPoint.proceed(joinPoint.getArgs());
            } catch(Throwable t) {
                throw Lombok.sneakyThrow(t);
            }

        });
    }

    @Around(value="@annotation(annotation)", argNames="joinPoint,annotation")
    public Object assertNoMidLock(ProceedingJoinPoint joinPoint, MediaObjectLocker.AssertNoMidLock annotation) {
        MediaObjectLocker.assertNoMidLock(joinPoint.toLongString());
        try {
            return joinPoint.proceed(joinPoint.getArgs());
        } catch(Throwable t) {
            throw Lombok.sneakyThrow(t);
        }
    }



    protected static MediaIdentifiable.Correlation getCorrelation(String method, Object object) {
        if (object == null) {
            return MediaIdentifiable.Correlation.NO_LOCK;
        }
        if (StringUtils.isNotBlank(method)) {
            try {
                final Method m = object.getClass().getMethod(method);
                return MediaIdentifiable.Correlation.mid(StringUtils.trim((String) m.invoke(object)));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
                throw new IllegalStateException();
            }
        } else {
            if (object instanceof CharSequence) {
                return  MediaIdentifiable.Correlation.mid(object.toString());
            }
            if (object instanceof MediaIdentifiable mediaIdentifiable) {
                final MediaIdentifiable.Correlation correlation = mediaIdentifiable.getCorrelation();
                if (correlation == null || correlation.getType() == MediaIdentifiable.Correlation.Type.HASH) {
                    boolean warn = true;
                    if (object instanceof MediaObject mediaObject) {
                        if (mediaObject.getId() == null) {
                            warn = false;
                        }
                    }

                    Slf4jHelper.log(log, warn ? Level.WARN : Level.DEBUG,"Object {} has no correlation id ({})", object, correlation);
                } else {
                    log.debug("{} has correlation {}", object, correlation);
                }

                return correlation;
            }
            throw new IllegalStateException("Object " + object + " is of unrecognized type " + (object == null ? "NULL" : object.getClass().getName() ));
        }
    }


    public static ScheduleEventIdentifier getSid(Object object) {
        if (object instanceof CharSequence charSequence) {
            return ScheduleEventIdentifier.parse(charSequence);
        }
        if (object instanceof ScheduleEventIdentifier scheduleEventIdentifier) {
            return scheduleEventIdentifier;
        }
        if (object instanceof ScheduleEvent scheduleEvent) {
            return scheduleEvent.getId();
        }
        throw new IllegalStateException();
    }
}
