package nl.vpro.domain.media.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class OwnableLists {


    public static <T extends Ownable> boolean containsDuplicateOwner(Iterable<T> newValues){
        if (newValues != null) {
            Map<OwnerType, AtomicInteger> counts = new HashMap<>();
            for (T v : newValues) {
                if (counts.computeIfAbsent(v.getOwner(), (a) -> new AtomicInteger(0)).incrementAndGet() > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T extends Ownable> Optional<T>  filter(SortedSet<T> intentions, OwnerType owner){
        if(intentions == null || intentions.isEmpty()) return Optional.empty();
        for (T intention : intentions) {
            if (intention.getOwner() == owner) {
                return Optional.of(intention);
            }
        }
        return Optional.of(intentions.first());
    };
}
