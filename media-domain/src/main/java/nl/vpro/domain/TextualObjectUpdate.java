package nl.vpro.domain;

import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.i18n.Locales;

/**
 * An object that has titles and descriptions, which are typed, but not necessary owned (unlike {@link TextualObject}).
 * This means that the object generally is used to update 'TextualObjects', since the user updating is associated with only one {@link OwnerType}.
 *
 * @param <T> The type of 'titles' in this object.
 * @param <D> The type of 'descriptions'.
 * @param <TO> The type of {@link #self()} which is returned by several setters, so they can easily be chained
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public interface TextualObjectUpdate<T extends TypedText, D extends TypedText, TO extends TextualObjectUpdate<T, D, TO>> {

    /**
     * Describes in which language the contained text objects are. This defaults to {@link Locales#DUTCH}.
     */
    default Locale getLanguage() {
        return Locales.DUTCH;
    }

    /**
     * How to create an instance of a title. This makes a lot of default methods possible.
     *
     * @since 5.5
     */
    BiFunction<String, TextualType, T> getTitleCreator();

    default TO addTitle(String title, @NonNull TextualType type) {
        if (getTitles() == null) {
            setTitles(new TreeSet<>());
        }
        T created = getTitleCreator().apply(title, type);
        boolean added = getTitles().add(created);
        return self();
    }


    /**
     * Sets title if already set, otherwise {@link #addTitle(String, TextualType)}
     * @since 5.11
     */
    default void setTitle(String title, @NonNull TextualType type) {
        if (getTitles() != null) {
            for (T t : getTitles()) {
                if (t.getType() == type) {
                    t.set(title);
                    return;
                }
            }
        }
        addTitle(title, type);
    }

    /**
     * Updates a title with a {@link Supplier}, if at least the given supplier is not null.
     */
    default TO setTitle(@Nullable Supplier<String> up, @NonNull TextualType type) {
        if (up != null) {
            setTitle(up.get(), type);
        }
        return self();
    }


    SortedSet<T> getTitles();

    void setTitles(SortedSet<T> titles);


    /**
     * Default implementation based on {@link #getTitles}, you may want to override this, if getTitles may modify
     * the object (it is e.g. required by jaxb that this will never return <code>null</code>
     */
    default boolean hasTitles() {
        return getTitles() != null && getTitles().size() > 0;
    }

    default TO addTitle(T title) {
        if (getTitles() == null) {
            setTitles(new TreeSet<>());
        }
        TextualObjects.addOrUpdate(getTitles(), title);
        return self();
    }

    default boolean removeTitle(T title) {
        return hasTitles() && getTitles().remove(title);
    }

    @SuppressWarnings("unchecked")
    default TO self() {
        return (TO) this;
    }


    default T findTitle(@NonNull TextualType type) {
        if (hasTitles()) {
            for (T title : getTitles()) {
                if (type == title.getType()) {
                    return title;
                }
            }
        }
        return null;
    }



    default String getMainTitle() {
        return TextualObjects.get(getTitles(), TextualType.MAIN);
    }


    default void setMainTitle(String title) {
        setTitle(title, TextualType.MAIN);
    }


    /**
     * Retrieves the first sub- or episode title. MIS distributes episode
     * titles. For internal use this episode title is handled as a subtitle.
     *
     * @return - the first subtitle
     */
    default String getSubTitle() {
        return TextualObjects.get(getTitles(), TextualType.SUB);
    }

    default String getShortTitle() {
        return TextualObjects.get(getTitles(), TextualType.SHORT);
    }

    default String getOriginalTitle() {
        return TextualObjects.get(getTitles(), TextualType.ORIGINAL);
    }

    default String getWorkTitle() {
        return TextualObjects.get(getTitles(), TextualType.WORK);
    }

    /**
     * Return the 'lexicographic title'. This is the title where the object normally would be sorted on.
     * This defaults to {@link #getMainTitle()} if no explicit value was set. If an explicit value is set, it may
     * e.g. be the same as {@link #getMainTitle()} but with leading articles ommitted.
     */
    default String getLexicoTitle() {
        return TextualObjects.getOptional(getTitles(), TextualType.LEXICO).orElse(getMainTitle());
    }

    /**
     * @since 2.1
     */
    default String getAbbreviatedTitle() {
        return TextualObjects.get(getTitles(), TextualType.ABBREVIATION);
    }


     /**
     * Sets description if already set, otherwise {@link #addDescription(String, TextualType)}
     * @since 5.11
     */
    default void setDescription(String description, @NonNull TextualType type) {
        if (getDescriptions() != null) {
            for (D d : getDescriptions()) {
                if (d.getType() == type) {
                    d.set(description);
                    return;
                }
            }
        }
        addDescription(description, type);
    }

    /**
     * Updates a description with a {@link Supplier}, if at least the given supplier is not null.
     */
    default TO setDescription(@Nullable  Supplier<String> up, @NonNull TextualType type) {
        if (up != null) {
            setDescription(up.get(), type);
        }
        return self();
    }

    /**
     * @since 5.5
     */
    BiFunction<String, TextualType, D> getDescriptionCreator();

    default TO addDescription(@Nullable String description, @NonNull TextualType type) {
        if (StringUtils.isNotEmpty(description)) {
            if (getDescriptions() == null) {
                setDescriptions(new TreeSet<>());
            }
            getDescriptions().add(getDescriptionCreator().apply(description, type));
        }
        return self();
    }


    SortedSet<D> getDescriptions();

    void setDescriptions(SortedSet<D> descriptions);


    /**
     * Default implementation based on {@link #getDescriptions()}, you may want to override this, if getDescriptions may modify
     * the object (it is e.g. required by hibernate that this will never return <code>null</code>
     */
    default boolean hasDescriptions() {
        return getDescriptions() != null && getDescriptions().size() > 0;
    }

    default TO addDescription(D description) {
        if (getDescriptions() == null) {
            setDescriptions(new TreeSet<>());
        }
        TextualObjects.addOrUpdate(getDescriptions(), description);
        return self();
    }

    default boolean removeDescription(D description) {
        return hasDescriptions() && getDescriptions().remove(description);
    }



    default boolean removeDescription(TextualType type) {
        boolean result = false;
        if (hasDescriptions()) {
            Iterator<D> i = getDescriptions().iterator();
            while (i.hasNext()) {
                D d = i.next();
                if (d.getType() == type) {
                    i.remove();
                    result = true;
                }

            }
        }
        return result;
    }



    default D findDescription(@NonNull TextualType type) {
        if (hasDescriptions()) {
            for (D description : getDescriptions()) {
                if (type == description.getType()) {
                    return description;
                }
            }
        }
        return null;
    }


    default String getMainDescription() {
        if (hasDescriptions()) {
            return getDescriptions().first().get();
        }
        return null;
    }

    default void setMainDescription(String description) {
        setDescription(description, TextualType.MAIN);
    }

    default String getSubDescription() {
        if (hasDescriptions()) {
            for (D description : getDescriptions()) {
                if (description.getType().equals(TextualType.SUB)) {
                    return description.get();
                }
            }
        }
        return null;
    }

    default String getShortDescription() {
        if (hasDescriptions()) {
            for (D description : getDescriptions()) {
                if (description.getType().equals(TextualType.SHORT)) {
                    return description.get();
                }
            }
        }
        return null;
    }
}
