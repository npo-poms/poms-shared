package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.MediaObject;

/**
 * Utilities related to updating {@link MediaObjectOwnableList}.
 *
 * @see  OwnableLists
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class MediaObjectOwnableLists {


    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    boolean addValue(@NonNull Set<P> set,
                     @NonNull Supplier<P> creator,
                     @NonNull I newValue,
                     @NonNull OwnerType owner) {
        Optional<P> match = set.stream().filter(o -> Objects.equals(o.getOwner(), owner)).findFirst();
        if (match.isPresent() && match.get().getValues().contains(newValue)) {
            return false;
        } else if (match.isPresent()) {
            newValue.setParent(match.get());
            return match.get().getValues().add(newValue);
        } else {
            P newList = creator.get();
            newValue.setParent(newList);
            newList.getValues().add(newValue);
            return set.add(newList);
        }
    }


    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    MediaObject addOrUpdateOwnableList(@NonNull MediaObject parent, @NonNull Collection<P> list, @NonNull P newOwnableList) {
        Optional<P> existing = OwnableLists.filterByOwner(list, newOwnableList.getOwner());
        if (existing.isPresent()) {
            P existingList = existing.get();
            if ( ! Objects.equals(existingList.getValues(), newOwnableList.getValues())) {
                existing.get().getValues().clear();
                newOwnableList.getValues().forEach(nv -> {
                    nv.setParent(existingList);
                    existingList.add(nv);
                });

            }
        } else {
            newOwnableList.setParent(parent);
            list.add(newOwnableList);
        }
        return parent;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    void addOrUpdateOwnableListForOwner(@NonNull MediaObject parent, @NonNull SortedSet<P> toUpdate,@Nullable  SortedSet<P> incoming, OwnerType forOwner) {
        if(incoming == null) {
            // Ignore it completely, this mainly is for the case that a media update xml was incoming with not values for the given set at all (the xml wrapper was missing)
            // indicating that client is at all interested or aware of the feature, and we don't want to empty the value for the owner
        } else  {
            Optional<P> is = OwnableLists.filterByOwner(incoming, forOwner);
            if (is.isPresent()) {
                addOrUpdateOwnableList(parent, toUpdate, is.get());
            } else {
                log.debug("No value found for {} found in {}", forOwner, incoming);
            }
        }
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
    void set(@NonNull MediaObject parent, @NonNull Set<P> existingCollection, @NonNull Set<P> newCollection) {
        existingCollection.clear();
        for (P i : newCollection) {
            addOrUpdateOwnableList(parent, existingCollection, i.clone());
        }
    }

    public static <P extends MediaObjectOwnableList<P, I>, I extends MediaObjectOwnableListItem<I, P>>
    void setIfNotNull(@NonNull MediaObject parent, @Nullable  Set<P> existingCollection, @NonNull Set<P> newCollection) {
        if (existingCollection != null) {
            set(parent, existingCollection, newCollection);
        }
    }

    public static <OL extends MediaObjectOwnableList<OL, I>, I extends MediaObjectOwnableListItem<I, OL>>
    SortedSet<OL> expandOwnedList(
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
