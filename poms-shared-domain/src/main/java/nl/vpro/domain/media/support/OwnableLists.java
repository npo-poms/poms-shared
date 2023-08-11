package nl.vpro.domain.media.support;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.validation.NoDuplicateOwner;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class OwnableLists {

    private OwnableLists() {
    }

    /**
     * TODO: I think this may be superseded by using {@link NoDuplicateOwner}
     */
    public static <T extends Ownable> boolean containsDuplicateOwner(@Nullable Iterable<T> newValues){
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
     * @param <T> Type of the elements
     * @return the element matching the given owner or the one to display if nothing matches.
     */
    public static <T extends Ownable> Optional<T> filterByOwnerOrFirst(@Nullable SortedSet<T> ownableSet, @NonNull OwnerType owner){
        Optional<T> filtered = filterByOwner(ownableSet, owner);

        if (filtered.isEmpty() && ownableSet != null && ! ownableSet.isEmpty()) {
            return Optional.of(ownableSet.first());
        }
        return filtered;
    }
    /**
     * Return the element to display
     * @param ownableSet the collection of all the sets
     * @param owner to match
     * @param <T> Type of the elements
     * @return the element matching the given owner or the one to display if nothing matches.
     */
    public static <T extends Ownable> Optional<T>  filterByOwner(@Nullable Collection<T> ownableSet, @NonNull OwnerType owner){
        if(ownableSet == null || ownableSet.isEmpty()) {
            return Optional.empty();
        }
        for (T intention : ownableSet) {
            if (intention.getOwner() == owner) {
                return Optional.of(intention);
            }
        }
        return Optional.empty();
    }
}
