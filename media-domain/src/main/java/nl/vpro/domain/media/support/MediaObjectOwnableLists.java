package nl.vpro.domain.media.support;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.MediaObject;

import static nl.vpro.domain.media.support.OwnableLists.containsDuplicateOwner;

/**
 * Utilities related to updating {@link MediaObjectOwnableList}.
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class MediaObjectOwnableLists {


    public static  <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    SortedSet<P> createIfNull(SortedSet<P> set) {
        if(set == null) {
            set = new TreeSet<>();
        }
        return set;
    }

    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    boolean addValue(@NonNull Set<P> set,
                     @NonNull Supplier<P> creator,
                     @NonNull I newValue,
                     @NonNull OwnerType owner) {
        Optional<P> match = set.stream().filter(o -> Objects.equals(o.getOwner(), owner)).findFirst();
        if (match.isPresent() && match.get().getValues().contains(newValue)) {
            return false;
        }
        else if (match.isPresent()) {
            newValue.setParent(match.get());
            return match.get().getValues().add(newValue);
        }
        else {
            P newList = creator.get();
            newValue.setParent(newList);
            newList.getValues().add(newValue);
            return set.add(newList);
        }
    }

    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    MediaObject addOwnableList(@NonNull MediaObject parent, @NonNull Collection<P> list, @NonNull P newOwnableList) {
        list.removeIf(existing -> existing.getOwner() == newOwnableList.getOwner());
        newOwnableList.setParent(parent);
        list.add(newOwnableList);
        return parent;
    }

    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    boolean remove(
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
    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    boolean remove(
        Set<P> set,
        @NonNull OwnerType owner
    ) {
        if (set == null) {
            return false;
        }
        boolean changed = false;
        Iterator<P> i = set.iterator();
        while (i.hasNext()) {
            P candidate = i.next();
            if (candidate.getOwner() == owner) {
                candidate.getValues().forEach(v -> {
                    //v.setParent(null);
                });
                //candidate.getValues().clear();
                i.remove();
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Find an MediaObjectOwnableListItem given id and owner
     * @param list collection to search into
     * @param id
     * @param owner
     * @return Optional/<MediaObjectOwnableListItem/> empty if nothing matched
     */
    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    Optional<I> find(Collection<P> list, @NonNull Long id, @NonNull OwnerType owner){
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        final Optional<List<I>> maybeValues = list.stream()
            .filter(owned -> owned.getOwner().equals(owner))
            .findAny()
            .map(OwnableList::getValues);

        if(maybeValues.isPresent()) {
            final Optional<I> maybeLocationFound =
                maybeValues.get().stream().filter(
                    v -> id.equals(v.getId())
                ).findAny();

            return maybeLocationFound;
        }
        return Optional.empty();
    }

    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    void set(@NonNull MediaObject parent, @NonNull Collection<P> existingCollection, @NonNull Collection<P> newCollection) {
        if (containsDuplicateOwner(newCollection)) {
            throw new IllegalArgumentException("The list you want to set has a duplicate owner: " + newCollection);
        }
        existingCollection.clear();
        for (P i : newCollection) {
            addOwnableList(parent, existingCollection, i.clone());
        }
    }

    public static <OL extends MediaObjectOwnableList<OL, I>, I extends MediaObjectOwnableListItem<I, OL>> SortedSet<OL> expandOwnedList(
            SortedSet<OL> values,
            BiFunction<OwnerType, List<I>, OL> creator,
            List<OwnerType> ownersToExpand) {

        if(values == null || values.isEmpty()) return null;
        SortedSet<OL> additions = new TreeSet<>();
        for(OwnerType owner: ownersToExpand){
            if(values.stream().anyMatch(value -> value.getOwner() == owner)) continue;
            additions.add(creator.apply(owner, values.first().getValues()));
        }
        values.addAll(additions);
        return values;
    }

}
