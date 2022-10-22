package nl.vpro.domain.media;

import lombok.Lombok;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

// DeclarePrecedence not supported by spring AOP, this is just extended in media project with a spring @Order annotation in stead.
//@DeclarePrecedence("nl.vpro.domain.media.MediaObjectLockerAspect, org.springframework.transaction.aspectj.AnnotationTransactionAspect, *")
public abstract class MediaObjectLockerAspect  {


    @Around(value="@annotation(annotation)", argNames="joinPoint,annotation")
    public Object lockMid(ProceedingJoinPoint joinPoint, MediaObjectLocker.Mid annotation) {
        Object media = joinPoint.getArgs()[annotation.argNumber()];
        String method = annotation.method();
        MediaIdentifiable.Correlation correlation = getCorrelation(method, media);

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
        if (StringUtils.isNotBlank(method)) {
            try {
                Method m = object.getClass().getMethod(method);
                return MediaIdentifiable.Correlation.mid(StringUtils.trim((String) m.invoke(object)));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
                throw new IllegalStateException();
            }
        } else {
            if (object instanceof CharSequence) {
                return  MediaIdentifiable.Correlation.mid(object.toString());
            }
            if (object instanceof MediaIdentifiable) {
                MediaIdentifiable.Correlation correlation = ((MediaIdentifiable) object).getCorrelation();
                if (correlation == null || correlation.getType() == MediaIdentifiable.Correlation.Type.HASH) {
                    boolean warn = true;
                    if (object instanceof MediaObject) {
                        if (((MediaObject) object).getId() == null) {
                            warn = false;
                        }
                    }

                    Slf4jHelper.log(log, warn ? Level.WARN : Level.DEBUG,"Object {} has no correlation id ({})", object, correlation);
                } else {
                    log.debug("{} has correlation {}", object, correlation);
                }

                return correlation;
            }
            throw new IllegalStateException("Object is of unrecognized type " + object);
        }
    }


    public static ScheduleEventIdentifier getSid(Object object) {
        if (object instanceof CharSequence) {
            return ScheduleEventIdentifier.parse(object.toString());
        }
        if (object instanceof ScheduleEventIdentifier) {
            return (ScheduleEventIdentifier) object;
        }
        if (object instanceof ScheduleEvent) {
            return ((ScheduleEvent) object).getId();
        }
        throw new IllegalStateException();
    }
}
