package nl.vpro.domain;

import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import nl.vpro.domain.media.support.TextualType;
import nl.vpro.i18n.Locales;

/**
 * An object that has titles and descriptions, which are typed, but not necessary owned (unlike {@link TextualObject}). This means that the object generally is used to update 'TextualObjects', since the user updating is associated with only one {@link nl.vpro.domain.media.support.OwnerType}.
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

    TO addTitle(String title, TextualType type);

    default void setTitle(String title, TextualType type) {
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

    SortedSet<T> getTitles();

    void setTitles(SortedSet<T> titles);


    default boolean hasTitles() {
        return getTitles() != null && getTitles().size() > 0;
    }

    default TO addTitle(T title) {
        if (getTitles() == null) {
            setTitles(new TreeSet<T>());
        }
        getTitles().add(title);
        return self();
    }

    default boolean removeTitle(T title) {
        return getTitles() != null && getTitles().remove(title);
    }

    default TO self() {
        return (TO) this;
    }


    default T findTitle(TextualType type) {
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


    default void setDescription(String description, TextualType type) {
        if (getDescriptions() != null) {
            for (D t : getDescriptions()) {
                if (t.getType() == type) {
                    t.set(description);
                    return;
                }
            }
        }
        addDescription(description, type);
    }


    TO addDescription(String description, TextualType type);


    SortedSet<D> getDescriptions();

    void setDescriptions(SortedSet<D> descriptions);


    default boolean hasDescriptions() {
        return getDescriptions() != null && getDescriptions().size() > 0;
    }

    default TO addDescription(D description) {
        if (getDescriptions() == null) {
            setDescriptions(new TreeSet<D>());
        }
        getDescriptions().add(description);
        return self();
    }

    default boolean removeDescription(D description) {
        return getDescriptions().remove(description);
    }



    default D findDescription(TextualType type) {
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
