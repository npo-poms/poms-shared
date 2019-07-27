package nl.vpro.domain.media.support;

import java.util.*;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.Child;
import nl.vpro.domain.Identifiable;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class MediaObjectOwnableLists {

    public static  <P extends MediaObjectOwnableList<P, I>, I extends Comparable<I> & Child<P> & Identifiable<Long>> SortedSet<P> createIfNull(SortedSet<P> set) {
        if(set == null) {
            set = new TreeSet<>();
        }
        return set;
    }

    public static <P extends MediaObjectOwnableList<P, I>, I extends Comparable<I> & Child<P> & Identifiable<Long>> boolean add(
        @NonNull Set<P> set,
        @NonNull Supplier<P> creator,
        @NonNull I newValue,
        @NonNull OwnerType owner) {
        Optional<P> match = set.stream().filter(o -> Objects.equals(o.getOwner(), owner)).findFirst();
        if (match.isPresent() && match.get().getValues().contains(newValue)) {
            return false;
        }

        if (match.isPresent()) {
            newValue.setParent(match.get());
            return match.get().getValues().add(newValue);
        } else {
            P newList = creator.get();
            newValue.setParent(newList);
            newList.getValues().add(newValue);
            return set.add(newList);
        }
    }

    public static <P extends MediaObjectOwnableList<P, I>, I extends Comparable<I> & Child<P> & Identifiable<Long>> boolean remove(
        Set<P> set,
        @NonNull I value,
        @NonNull OwnerType owner


    ) {
        if (set == null) {
            return false;
        }
        final Optional<P> maybeValues = set.stream()
            .filter(owned -> owned.getOwner().equals(owner))
            .findAny();

        if(maybeValues.isPresent()) {
            P list = maybeValues.get();
            return list.getValues().remove(value);
        }
        return false;
    }

}
