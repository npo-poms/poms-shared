package nl.vpro.domain.media.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class Ownables {


    public static <T extends OwnableR> boolean containsDuplicateOwner(@Nonnull Set<T> newValues){
        Map<OwnableR, AtomicInteger> counts = new HashMap<>();
        for (T v : newValues) {
            if (counts.computeIfAbsent(v, (a) -> new AtomicInteger(0)).incrementAndGet() > 1) {
                return true;
            }
        }
        return false;
    }
}
