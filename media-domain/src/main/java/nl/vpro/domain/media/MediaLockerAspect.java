package nl.vpro.domain.media;

import lombok.Lombok;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This is an idea to make locking on mid easier.
 *
 * Just annotate your method with {@link MediaObjectLocker.Mid} and it should automaticly lock the mid if it isn't yet.
 *
 *
 * TODO: This is as yet purely experimental, and I've not yet checked in any methods using this annotation
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Aspect
public class MediaLockerAspect {

    @Around("@annotation(nl.vpro.domain.media.MediaObjectLocker.Mid)")
    public Object lockMid(ProceedingJoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        MediaObjectLocker.Mid midLock = signature.getMethod().getAnnotation(MediaObjectLocker.Mid.class);
        Object media = joinPoint.getArgs()[midLock.argNumber()];
        String mid = getMid(media);
        String reason = midLock.reason();

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
