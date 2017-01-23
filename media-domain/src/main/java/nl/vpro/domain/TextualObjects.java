package nl.vpro.domain;

import java.util.*;
import java.util.stream.Collectors;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.util.ResortedSortedSet;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class TextualObjects {

    // some methods working on collections of 'OwnedText' objects (think of titles and descriptions)

    public static <OT extends OwnedText> String get(Collection<OT> titles, TextualType... types) {
        return get(titles, "", types);
    }

    public static <OT extends OwnedText> String get(Collection<OT> titles, String defaultValue, TextualType... types) {
        OT title = getObject(titles, types);
        return title == null ? defaultValue : title.get();
    }
    public static <OT extends OwnedText> String get(Collection<OT> titles, OwnerType owner, TextualType type) {
        for (OT title : titles) {
            if (title.getOwner() == owner && title.getType() == type) {
                return title.get();
            }
        }
        return "";
    }

    public static <OT extends OwnedText> OT getObject(Collection<OT> titles, TextualType... types) {
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


    public static <OT extends OwnedText> Collection<OT> getObjects(Collection<OT> titles, TextualType... types) {
        List<OT> returnValue = new ArrayList<>();
        if (titles != null) {
            for (OT title : titles) {
                for (TextualType type : types) {
                    if (type == title.getType()) {
                        returnValue.add(title);
                    }
                }
            }
        }
        return returnValue;
    }

    // some methods working TextualObjects themselves.

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>>  OwnerType[] findOwnersForTextFields(TO media) {
        SortedSet<OwnerType> result = new TreeSet<>();
        for (T title : media.getTitles()) {
            result.add(title.getOwner());
        }
        for (D description : media.getDescriptions()) {
            result.add(description.getOwner());
        }
        return result.toArray(new OwnerType[result.size()]);
    }


    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getTitle(TO media, OwnerType owner, TextualType type) {
        return get(media.getTitles(), owner, type);
    }

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getDescription(TO media, OwnerType owner, TextualType type) {
        return get(media.getDescriptions(), owner, type);
    }

    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> String getDescription(TO media, TextualType... types) {
        return get(media.getDescriptions(), types);
    }


    /**
     * Sets the owner of all titles, descriptions, locations and images found in given MediaObject
     */
    public static <T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> void forOwner(TO media, OwnerType owner) {
        for (T title : media.getTitles()) {
            title.setOwner(owner);
        }
        for (D description : media.getDescriptions()) {
            description.setOwner(owner);
        }
    }

    public static <T extends Ownable> List<T> filter(Collection<T> ownables, OwnerType owner) {
        return ownables.stream().filter(item -> item.getOwner() == owner).collect(Collectors.toList());
    }


    public static <S> SortedSet<S> sorted(Set<S> set) {
        if (set == null) {
            return null;
        }
        if (set instanceof SortedSet) {
            //noinspection unchecked
            return (SortedSet) set;
        } else {
            return new ResortedSortedSet<S>(set);
        }
    }


}
