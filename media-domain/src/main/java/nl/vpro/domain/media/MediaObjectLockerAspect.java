package nl.vpro.domain.media;

import lombok.Lombok;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * This is an idea to make locking on mid easier.
 *
 * Just annotate your method with {@link MediaObjectLocker.Mid} and it should automaticly lock the mid if it isn't yet.
 *
 ** @author Michiel Meeuwissen
 * @since 5.8
 */
@Aspect
public class MediaObjectLockerAspect {

    @Around(value="@annotation(annotation)", argNames="joinPoint,annotation")
    public Object lockMid(ProceedingJoinPoint joinPoint, MediaObjectLocker.Mid annotation) {
        Object media = joinPoint.getArgs()[annotation.argNumber()];
        String mid = getMid(media);
        String reason = annotation.reason();
        if (StringUtils.isEmpty(reason)) {
            reason = joinPoint.getSignature().getDeclaringType().getSimpleName() + "#" + joinPoint.getSignature().getName();
        }

        return MediaObjectLocker.runAlone(mid, reason, () -> {
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
            return ((MediaIdentifiable) object).getMid();
        }
        throw new IllegalStateException();
    }
}
