package nl.vpro.domain.media;

import lombok.Lombok;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * This is an idea to make locking on mid easier.
 *
 * Just annotate your method with {@link MediaObjectLocker.Mid} and it should automaticly lock the mid if it isn't yet.
 *
 *
 ** @author Michiel Meeuwissen
 * @since 5.8
 */
@Aspect
@Slf4j
//@DeclarePrecedence("nl.vpro.domain.media.MediaObjectLockerAspect, org.springframework.transaction.aspectj.AnnotationTransactionAspect, *")
public abstract class MediaObjectLockerAspect  {

    @Around(value="@annotation(annotation)", argNames="joinPoint,annotation")
    public Object lockMid(ProceedingJoinPoint joinPoint, MediaObjectLocker.Mid annotation) {
        Object media = joinPoint.getArgs()[annotation.argNumber()];
        String mid = getMid(media);
        String reason = annotation.reason();
        if (StringUtils.isEmpty(reason)) {
            reason = joinPoint.getSignature().getDeclaringType().getSimpleName() + "#" + joinPoint.getSignature().getName();
        }

        return MediaObjectLocker.withMidLock(mid, reason, () -> {
            try {
                return joinPoint.proceed(joinPoint.getArgs());
            } catch(Throwable t) {
                throw Lombok.sneakyThrow(t);
            }

        });

    }


    @Around(value="@annotation(annotation)", argNames="joinPoint,annotation")
    public Object lockSid(ProceedingJoinPoint joinPoint, MediaObjectLocker.Sid annotation) {
        Object scheduleEvent = joinPoint.getArgs()[annotation.argNumber()];
        ScheduleEventIdentifier sid = getSid(scheduleEvent);
        String reason = annotation.reason();
        if (StringUtils.isEmpty(reason)) {
            reason = joinPoint.getSignature().getDeclaringType().getSimpleName() + "#" + joinPoint.getSignature().getName();
        }

        return MediaObjectLocker.withKeyLock(sid, reason, () -> {
            try {
                return joinPoint.proceed(joinPoint.getArgs());
            } catch(Throwable t) {
                throw Lombok.sneakyThrow(t);
            }

        });

    }


    public static String getMid(Object object) {
        if (object instanceof CharSequence) {
            return object.toString();
        }
        if (object instanceof MediaIdentifiable) {
            String mid = ((MediaIdentifiable) object).getMid();
            if (mid == null) {
                log.warn("Object {} has no mid", object);
            }

            return mid;
        }
        throw new IllegalStateException();
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
