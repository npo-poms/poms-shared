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


    /**
     * TODO: I think this may be superseded by using {@link @NoDuplicateOwner}
     */
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

    /**
     * Return the element to display
     * @param ownableSet the collection of all the sets
     * @param owner to match
     * @param <T>
     * @return the element matching the given owner or the one to display if nothing matches.
     */
    public static <T extends Ownable> Optional<T>  filterByOwner(SortedSet<T> ownableSet, OwnerType owner){
        if(ownableSet == null || ownableSet.isEmpty()) {
            return Optional.empty();
        }
        for (T intention : ownableSet) {
            if (intention.getOwner() == owner) {
                return Optional.of(intention);
            }
        }
        return Optional.of(ownableSet.first());
    };
}
