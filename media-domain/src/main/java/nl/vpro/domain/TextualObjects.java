package nl.vpro.domain;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.functional.TriFunction;

import nl.vpro.domain.media.support.*;
import nl.vpro.util.ResortedSortedSet;


/**
 * Utilities related to {@link TextualObject}s
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class TextualObjects {

    private TextualObjects() {
    }

    // some methods working on collections of 'OwnedText' objects (think of titles and descriptions)

    /**
     * Finds the first text in a collection of {@link TypedText}s with one of the given types. Wrapped in an optional, so never returns {@code null}
     */
    public static <OT extends TypedText> Optional<String> getOptional(Collection<OT> titles, TextualType... types) {
        return Optional.ofNullable(
            getObject(titles, types)
        ).map(TypedText::get);
    }


    public static <OT extends TypedText> String get(Collection<? extends OT> titles, String defaultValue, TextualType... types) {
        OT title = getObject(titles, types);
        return title == null ? defaultValue : title.get();
    }


    public static <OT extends OwnedText> SortedSet<OT> expand(
        Collection<OT> texts,
        TriFunction<String, OwnerType, TextualType, OT> creator,
        List<TextualType> types,
        List<OwnerType> owners) {
        SortedSet<OT> result = new TreeSet<>(texts);
        for (TextualType textualType : types) {
            for (OwnerType ownerType : owners) {
                expand(texts, textualType, ownerType).ifPresent(ot -> {
                    if (ot.getType() != textualType || ot.getOwner() != ownerType) {
                        result.add(creator.apply(ot.get(), ownerType, textualType));
                    }
                    }
                );
            }
        }
        return result;
    }

    /**
     * Creates a new sorted set of titles where:
     * <ul>
     * <li>All Textual Types from {@link TextualType#TITLES} are filled according to the business logic of POMs.
     * This means mainly that the value for LEXICO is filled with MAIN if empty otherwise</li>
     * <li>For all those fields it is assured that values with owner types {@link OwnerType#ENTRIES} are added if they are not present yet,
     * according to the business logic of POMS. This means that if there is title of a certain type, than at least it is present for owners BROADCASTER and NPO too.</li>
     * </ul>
     */
    public static <OT extends OwnedText> SortedSet<OT> expandTitles(
        Collection<OT> texts,
        TriFunction<String, OwnerType, TextualType, OT> creator) {
        return expand(texts,
            creator,
            TextualType.TITLES,
            OwnerType.ENTRIES
        );
    }

    public static <T extends OwnedText> SortedSet<T> expandTitles(TextualObject<T, ?, ?> textualObject) {
        return expandTitles(textualObject.getTitles(), textualObject.getOwnedTitleCreator());
    }

    public static <T extends OwnedText> SortedSet<T> expandTitlesMajorOwnerTypes(TextualObject<T, ?, ?> textualObject) {
        SortedSet<T> result = expandTitles(textualObject.getTitles(), textualObject.getOwnedTitleCreator());
        result.removeIf(t -> !OwnerType.ENTRIES.contains(t.getOwner()));
        return result;
    }


    public static <T extends OwnedText> Map<OwnerType, SortedSet<BasicTypedText>>
    expandTitlesAsMap(TextualObject<T, ?, ?> textualObject) {
        Map<OwnerType, SortedSet<BasicTypedText>> result = new HashMap<>();

        for (OwnedText ot : expandTitles(textualObject.getTitles(), textualObject.getOwnedTitleCreator())) {
            SortedSet<BasicTypedText> set = result.computeIfAbsent(ot.getOwner(), o -> new TreeSet<>());
            set.add(new BasicTypedText(ot));
        }
        return result;
    }

    public static <OT extends OwnedText> SortedSet<OT> expandDescriptions(
        Collection<OT> texts,
        TriFunction<String, OwnerType, TextualType, OT> creator) {
        return expand(texts,
            creator,
            TextualType.DESCRIPTIONS,
            OwnerType.ENTRIES);
    }

    public static <D extends OwnedText> SortedSet<D> expandDescriptions(TextualObject<?, D, ?> textualObject) {
        return expandDescriptions(textualObject.getDescriptions(), textualObject.getOwnedDescriptionCreator());
    }


    public static <OT extends OwnedText> Optional<String> getOptional(Collection<OT> titles, OwnerType owner, TextualType type) {
        for (OT title : titles) {
            if (title.getOwner() == owner && title.getType() == type) {
                return Optional.of(title.get());
            }
        }
        return Optional.empty();
    }

    public static <OT extends OwnedText> Optional<String> get(Collection<OT> titles, Comparator<OwnerType> ownerType, TextualType... type) {
        return getObjects(titles, ownerType, type).stream().map(OwnedText::get).findFirst();
    }

    public static <OT extends TypedText> OT getObject(Collection<OT> titles, TextualType... types) {
        if (titles != null) {
            for (OT title : titles) {
                for (TextualType type : types) {
                    if (type == title.getType()) {
                        return title;
                    }
                }
            }
        }
        return null;
    }


    public static Collection<? extends OwnedText> getObjects(Collection<? extends OwnedText> titles, TextualType... types) {
        Comparator<OwnerType> comparator = Comparator.naturalOrder();
        return getObjects(titles, comparator, types);
    }


    protected static <OT extends OwnedText> Comparator<OT> getComparator(Comparator<OwnerType> ownerTypeComparator) {
        return (o1, o2) -> {
            int result = o1.getType().compareTo(o2.getType());
            if (result == 0) {
                return ownerTypeComparator.compare(o1.getOwner(), o2.getOwner());
            } else {
                return result;
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <OT extends OwnedText> Collection<? extends OwnedText> getObjects(
        Collection<? extends OT> titles,
        Comparator<OwnerType> ownerTypeComparator,
        TextualType... types) {


        List<OT> returnValue = new ArrayList<>();
        if (titles == null) {
            return returnValue;
        }
        Comparator<OT> comparator = getComparator(ownerTypeComparator);
        List<OT> list;
        if (titles instanceof List) {
            list = (List<OT>) titles;
        } else {
            list = new ArrayList<>(titles);
        }
        list.sort(comparator);
        for (OT title : list) {
            for (TextualType type : types) {
                if (type == title.getType()) {
                    returnValue.add(title);
                }
            }
        }
        return returnValue;
    }

    // some methods working TextualObjects themselves.

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> OwnerType[] findOwnersForTextFields(TO media) {
        SortedSet<OwnerType> result = new TreeSet<>();
        for (T title : media.getTitles()) {
            result.add(title.getOwner());
        }
        for (D description : media.getDescriptions()) {
            result.add(description.getOwner());
        }
        return result.toArray(new OwnerType[0]);
    }


    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getTitle(
        @NonNull TO media,
        @NonNull OwnerType owner,
        @NonNull TextualType type) {
        Optional<String> opt = getOptional(media.getTitles(), owner, type);
        return opt.orElse("");
    }

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getDescription(
        @NonNull TO media,
        @NonNull OwnerType owner,
        @NonNull TextualType type) {
        Optional<String> opt = getOptional(media.getDescriptions(), owner, type);
        return opt.orElse("");
    }

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getDescription(
        @NonNull TO media,
        @NonNull TextualType... types) {
        Optional<String> opt = getOptional(media.getDescriptions(), types);
        return opt.orElse("");
    }


    /**
     * Sets the owner of all titles, descriptions, locations and images found in given MediaObject
     */
    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> void forOwner(
        @NonNull TO media,
        @NonNull OwnerType owner) {
        for (T title : media.getTitles()) {
            title.setOwner(owner);
        }
        for (D description : media.getDescriptions()) {
            description.setOwner(owner);
        }
    }

    public static <T extends MutableOwnable> List<T> filter(Collection<T> ownables, OwnerType owner) {
        return ownables.stream().filter(item -> item.getOwner() == owner).collect(Collectors.toList());
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <S extends Comparable<?>> SortedSet<@NonNull S> sorted(Set<@NonNull S> set) {
        if (set == null) {
            return null;
        }
        if (set instanceof SortedSet) {
            return (SortedSet) set;
        } else {
            return ResortedSortedSet.of(set);
        }
    }


    public static <OT extends TypedText> String get(Collection<OT> titles, TextualType... work) {
        return getOptional(titles, work)
            .orElse(null);
    }

    /**
     * Returns the value for a certain {@link TextualType} and {@link OwnerType}. This implements a fall back mechanism.
     * It takes the first value with matching owner and type. If none found, it will fall back to the highest OwnerType ({@link OwnerType#BROADCASTER} and degrades until one is found.
     * <p>
     * Furthermore if no 'LEXICO' typed values if found, the value for 'MAIN' will be used.
     */
    public static <OT extends OwnedText> Optional<OT> expand(
        @NonNull Collection<OT> titles,
        @NonNull TextualType textualType,
        @NonNull final OwnerType ownerType) {
        for (OT t : titles) {
            if (t.getType() == textualType && t.getOwner() == ownerType) {
                return Optional.of(t);
            }
        }
        OwnerType runningOwnerType = ownerType == OwnerType.first() ? OwnerType.down(OwnerType.first()) : OwnerType.first();
        while (runningOwnerType != OwnerType.last()) {
            for (OT t : titles) {
                if (t.getType() == textualType && t.getOwner() == runningOwnerType) {
                    return Optional.of(t);
                }
            }
            runningOwnerType = OwnerType.down(runningOwnerType);
        }
        if (textualType == TextualType.LEXICO) {
            return expand(titles, TextualType.MAIN, ownerType);
        }
        return Optional.empty();
    }

    public static <OT extends OwnedText> Optional<OT> expand(Collection<OT> titles, TextualType textualType) {
        return expand(titles, textualType, OwnerType.first());
    }

    /**
     * Give a collection, find the first object which equals the object we want to be in it.
     * If one found, the 'value' is copied to it.
     * If not, then the object is added to the collection.
     * <p>
     * TypedText's are commonly stored in SortedSet's, where equals matches only owner and type, not the value itself.
     *
     * @param titles Collection
     * @param add    the object to add or update.
     */
    public static <OT extends TypedText> boolean addOrUpdate(Collection<OT> titles, OT add) {
        Optional<OT> existing = titles.stream().filter(d -> d.equals(add)).findFirst();
        if (existing.isPresent()) {
            existing.get().set(add.get());
            return false;
        } else {
            titles.add(add);
            return true;
        }
    }

    /**
     * Copies all titles and descriptions from one {@link TextualObject} to another.
     *
     * @since 5.3
     */
    public static <
        T1 extends OwnedText, D1 extends OwnedText, TO1 extends TextualObject<T1, D1, TO1>,
        T2 extends OwnedText, D2 extends OwnedText, TO2 extends TextualObject<T2, D2, TO2>
        > void copy(
        TO1 from,
        TO2 to) {
        if (from.getTitles() != null) {
            for (T1 title : from.getTitles()) {
                to.setTitle(title.get(), title.getOwner(), title.getType());
            }
        }
        if (from.getDescriptions() != null) {
            for (D1 description : from.getDescriptions()) {
                to.setDescription(description.get(), description.getOwner(), description.getType());
            }
        }
    }

    /**
     * Copies all titles from one object to another.
     * <p>
     * Moved from ImportUtil, SecureUpdateImpl, MediaUpdaterImpl
     *
     * @param owner The owner type of the titles to consider
     * @param similarOwnerTypes If this array is given, a new title is not created if there is no title with the given owner, but there _is_ one with one of these owner types (and it has the same value)*
     *
     * @since 5.6
     */
    public static <T extends AbstractOwnedText<T>, D1 extends AbstractOwnedText<D1>,
        TO1 extends TextualObject<T, D1, TO1>,
        D2 extends AbstractOwnedText<D2>,
        TO2 extends TextualObject<T, D2, TO2>
        >
    void updateTitlesForOwner(TO1 incomingMedia, TO2 mediaToUpdate, OwnerType owner, OwnerType... similarOwnerTypes) {
        for (TextualType type : TextualType.values()) {
            T incomingTitle = incomingMedia.findTitle(owner, type);
            T titleToUpdate = mediaToUpdate.findTitle(owner, type);

            if (titleToUpdate == null && incomingTitle != null) {
                for (OwnerType similar : similarOwnerTypes) {
                    T otherTitle = mediaToUpdate.findTitle(similar, type);
                    if (otherTitle != null) {
                        if (Objects.equals(otherTitle.get(), incomingTitle.get())) {
                            return;
                        }
                    }
                }

            }

            if (incomingTitle != null && titleToUpdate != null) {

                titleToUpdate.set(incomingTitle.get());

            } else if (incomingTitle != null) {

                mediaToUpdate.addTitle(incomingTitle.get(), owner, type);

            } else if (titleToUpdate != null) {

                mediaToUpdate.removeTitle(titleToUpdate);

            }
        }
    }

    /**
     * Copies all descriptions from one object to another.
     * <p>
     * Moved from ImportUtil, SecureUpdateImpl, MediaUpdaterImpl
     *
     * @since 5.6
     */
    public static <T1 extends AbstractOwnedText<T1>, D extends AbstractOwnedText<D>,
        TO1 extends TextualObject<T1, D, TO1>,
        T2 extends AbstractOwnedText<T2>,
        TO2 extends TextualObject<T2, D, TO2>
        >
    void updateDescriptionsForOwner(TO1 incomingMedia, TO2 mediaToUpdate, OwnerType owner, OwnerType... similarOwnerTypes) {
        for (TextualType type : TextualType.values()) {
            D incomingDescription = incomingMedia.findDescription(owner, type);
            D descriptionToUpdate = mediaToUpdate.findDescription(owner, type);

             if (descriptionToUpdate == null && incomingDescription != null && similarOwnerTypes.length > 0) {
                for (OwnerType similar : similarOwnerTypes) {
                    D otherDescription = mediaToUpdate.findDescription(similar, type);
                    if (otherDescription != null) {
                        if (Objects.equals(otherDescription.get(), incomingDescription.get())) {
                            return;
                        }
                    }
                }

            }

            if (incomingDescription != null && descriptionToUpdate != null) {

                descriptionToUpdate.set(incomingDescription.get());

            } else if (incomingDescription != null) {

                mediaToUpdate.addDescription(incomingDescription.get(), owner, type);

            } else if (descriptionToUpdate != null) {

                mediaToUpdate.removeDescription(descriptionToUpdate);

            }
        }
    }

    /**
     * Copies all titles and descriptions from one {@link TextualObjectUpdate} to a {@link TextualObject}.
     *
     * @param owner The owner of the fields in the destination
     * @since 5.3
     */
    public static <
        T1 extends TypedText, D1 extends TypedText, TO1 extends TextualObjectUpdate<T1, D1, TO1>,
        T2 extends OwnedText, D2 extends OwnedText, TO2 extends TextualObject<T2, D2, TO2>
        > void copy(
        TO1 from,
        TO2 to,
        OwnerType owner
    ) {
        if (from.getTitles() != null) {
            for (T1 title : from.getTitles()) {
                to.addTitle(title.get(), owner, title.getType());
            }
        }
        if (from.getDescriptions() != null) {
            for (D1 description : from.getDescriptions()) {
                to.addDescription(description.get(), owner, description.getType());
            }
        }
    }


    /**
     * Copies all titles and descriptions from one {@link TextualObjectUpdate} to a {@link TextualObject}.
     *
     * @since 5.11
     */
    public static <
        T1 extends TypedText, D1 extends TypedText, TO1 extends TextualObjectUpdate<T1, D1, TO1>,
        T2 extends TypedText, D2 extends TypedText, TO2 extends TextualObjectUpdate<T2, D2, TO2>
        > void copy(
        TO1 from,
        TO2 to
    ) {
        if (from.getTitles() != null) {
            for (T1 title : from.getTitles()) {
                to.setTitle(title.get(), title.getType());
            }
        }
        if (from.getDescriptions() != null) {
            for (D1 description : from.getDescriptions()) {
                to.setDescription(description.get(), description.getType());
            }
        }
    }



    /**
     * Copies all titles and descriptions from one {@link TextualObjectUpdate} to a {@link TextualObject}.
     * Then, remove all titles and descriptions (of the given owner) which were not in the source object.
     *
     * @param owner The owner of the fields in the destination
     * @since 5.3
     */
    public static <
        T1 extends TypedText, D1 extends TypedText, TO1 extends TextualObjectUpdate<T1, D1, TO1>,
        T2 extends OwnedText, D2 extends OwnedText, TO2 extends TextualObject<T2, D2, TO2>
        >
    void copyAndRemove(
        TO1 from,
        TO2 to,
        OwnerType owner) {
        copy(from, to, owner);
        retainAll(to.getTitles(), from.getTitles(), owner);
        retainAll(to.getDescriptions(), from.getDescriptions(), owner);
    }

    /**
     * From a collection of {@link OwnedText}'s remove all all elements with certain owner, which are not in the source collection of {@link TypedText}'s.
     *
     * @param collection The collection to remove objects from
     * @param toRetain   The collection of texts which are to be retained in collection
     * @since 5.3
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public static <TO extends TypedText, TO2 extends OwnedText> void retainAll(
        Collection<TO2> collection,
        Collection<TO> toRetain,
        OwnerType owner) {
        if (toRetain != null) {
            collection.removeIf(t -> t.getOwner().equals(owner) && (!toRetain.contains(t)));
        }
    }

    /**
     * @param collection The collection to remove objects from
     * @param toRetain   The collection of texts which are to be retained in collection
     * @since 5.3
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public static <TO extends TypedText, TO2 extends TypedText> void retainAll(
        Collection<TO2> collection,
        Collection<TO> toRetain) {
        if (toRetain != null) {
            collection.removeIf(t -> (!toRetain.contains(t)));
        }
    }


    /**
     * Copy all texts from one collection of {@link TextualObjectUpdate} to another.
     * If the target collection is a {@link TextualObject} then you want to use {@link #copy(TextualObjectUpdate, TextualObject, OwnerType)}
     *
     * @since 5.3
     */
    public static <
        T1 extends TypedText, D1 extends TypedText, TO1 extends TextualObjectUpdate<T1, D1, TO1>,
        T2 extends TypedText, D2 extends TypedText, TO2 extends TextualObjectUpdate<T2, D2, TO2>
        > void copyToUpdate(
        @NonNull TO1 from,
        @NonNull TO2 to) {
        forUpdate(from.getTitles(), (t) -> to.setTitle(t.get(), t.getType()), () -> to.setTitles(null));
        forUpdate(from.getDescriptions(), (t) -> to.setDescription(t.get(), t.getType()), () -> to.setDescriptions(null));
    }

    /**
     * Copies all titles and descriptions from one {@link TextualObjectUpdate} to a {@link TextualObject}.
     * Then, remove all titles and descriptions (of the given owner) which were not in the source object.
     *
     * @since 8.2
     */
    public static <
        T1 extends TypedText, D1 extends TypedText, TO1 extends TextualObjectUpdate<T1, D1, TO1>,
        T2 extends OwnedText, D2 extends OwnedText, TO2 extends TextualObject<T2, D2, TO2>
        >
    void copyAndRemove(
        TO1 from,
        TO2 to) {
        copy(from, to);
        retainAll(to.getTitles(), from.getTitles());
        retainAll(to.getDescriptions(), from.getDescriptions());
    }



    /**
     * Copy all texts from  a  {@link TextualObject} to a  {@link TextualObjectUpdate}.
     * You can to specify an owner. Preferred are then values with owners equals or lower then this given owner, or if there are none, it will fall back from the top again.
     * <p>
     * In principal if the owner is {@link OwnerType#BROADCASTER} youu could also use {@link #copyToUpdate(TextualObjectUpdate, TextualObjectUpdate)}
     *
     * @since 5.9
     */
    public static <
        OT1 extends OwnedText, OD1 extends OwnedText, TO1 extends TextualObject<OT1, OD1, TO1>,
        T2 extends TypedText, D2 extends TypedText, TO2 extends TextualObjectUpdate<T2, D2, TO2>
        > void copyToUpdate(
        @NonNull TO1 from,
        @NonNull TO2 to,
        @NonNull  OwnerType owner) {
        forUpdate(from.getTitles(), (t) -> to.setTitle(t.get(), t.getType()), () -> to.setTitles(null), owner);
        forUpdate(from.getDescriptions(), (t) -> to.setDescription(t.get(), t.getType()), () -> to.setDescriptions(null), owner);
    }



    /**
     * @since 5.8
     */
    protected static <T extends TypedText> void forUpdate(
        Collection<T> collection,
        Consumer<T> consumer,
        Runnable ifNull) {
        if (collection != null) {
            TextualType type = null;
            for (T title : collection) {
                if (Objects.equals(type, title.getType())) {
                    continue;
                }
                type = title.getType();
                consumer.accept(title);
            }
        } else {
            ifNull.run();
        }
    }

      /**
     * @since 5.9
     */
    protected static <T extends OwnedText> void forUpdate(
        Collection<T> collection,
        Consumer<T> consumer,
        Runnable ifNull,
        OwnerType owner) {
        if (collection != null) {
            Map<TextualType, T> titles = new HashMap<>();
            for (T title : collection) {
                T previous = titles.get(title.getType());

                if (previous == null || previous.getOwner().ordinal() < owner.ordinal()) {
                    titles.put(title.getType(), title);
                }
            }
            titles.values().forEach(consumer);
        } else {
            ifNull.run();
        }
    }


    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> void removeEmptyValues(
        @NonNull TO textualObject) {
        {
            Collection<T> toRemove = new ArrayList<>();
            for (T t : textualObject.getTitles()) {
                if (StringUtils.isEmpty(t.get())) {
                    toRemove.add(t);
                }
            }
            for (T t : toRemove) {
                textualObject.removeTitle(t);
            }

        }
        {
            Collection<D> toRemove = new ArrayList<>();
            for (D d : textualObject.getDescriptions()) {
                if (StringUtils.isEmpty(d.get())) {
                    toRemove.add(d);
                }
            }
            for (D d : toRemove) {
                textualObject.removeDescription(d);
            }

        }
    }

}
