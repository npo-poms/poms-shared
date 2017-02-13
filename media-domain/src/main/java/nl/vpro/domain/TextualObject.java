package nl.vpro.domain;

import java.util.Locale;
import java.util.SortedSet;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.i18n.Locales;

/**
 * Represents an object having titles and descriptions.
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface TextualObject<T extends OwnedText<T>, D extends OwnedText<D>, TO extends TextualObject<T, D, TO>> {

    default Locale getLanguage() {
        return Locales.DUTCH;
    }

    SortedSet<T> getTitles();

    void setTitles(SortedSet<T> titles);

    TO addTitle(String title, OwnerType owner, TextualType type);

    default boolean hasTitles() {
        return getTitles() != null && getTitles().size() > 0;
    }

    default TO addTitle(T title) {
        getTitles().add(title);
        return self();
    }
    default boolean removeTitle(T title) {
        return getTitles().remove(title);
    }

    default TO self() {
        return (TO) this;
    }

    default boolean removeTitle(OwnerType owner, TextualType type) {
        if (hasTitles()) {
            return getTitles().removeIf(title -> title.getOwner().equals(owner) && title.getType() == type);

        }
        return false;
    }

    default TO removeTitlesForOwner(OwnerType owner) {
        if (hasTitles()) {
            getTitles().removeIf(title -> title.getOwner().equals(owner));
        }
        return self();
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

    default T findTitle(OwnerType owner, TextualType type) {
        if (hasTitles()) {
            for (T title : getTitles()) {
                if (owner == title.getOwner() && type == title.getType()) {
                    return title;
                }
            }
        }
        return null;
    }



    default String getMainTitle() {
        return TextualObjects.get(getTitles(), TextualType.MAIN);
    }

    default void setMainTitle(String mainTitle) {
        addTitle(mainTitle, OwnerType.BROADCASTER, TextualType.MAIN);
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

    default String getLexicoTitle() {
        return TextualObjects.getOptional(getTitles(), TextualType.LEXICO).orElse(getMainTitle());
    }

    /**
     * @since 2.1
     */
    default String getAbbreviatedTitle() {
        return TextualObjects.get(getTitles(), TextualType.ABBREVIATION);
    }

    SortedSet<D> getDescriptions();
    void setDescriptions(SortedSet<D> descriptions);

    TO addDescription(String description, OwnerType owner, TextualType type);

    default boolean hasDescriptions() {
        return getDescriptions() != null && getDescriptions().size() > 0;
    }

    default TextualObject addDescription(D description) {
        getDescriptions().add(description);
        return self();
    }
    default boolean removeDescription(D description) {
        return getDescriptions().remove(description);
    }

    default boolean removeDescription(OwnerType owner, TextualType type) {
        if (hasDescriptions()) {
            return getDescriptions().removeIf(description -> description.getOwner().equals(owner) && description.getType() == type);

        }
        return false;
    }

    default TextualObject removeDescriptionsForOwner(OwnerType owner) {
        if (hasDescriptions()) {

            getDescriptions().removeIf(description -> description.getOwner().equals(owner));
        }
        return this;
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

    default D findDescription(OwnerType owner, TextualType type) {
        if (hasDescriptions()) {
            for (D description : getDescriptions()) {
                if (owner == description.getOwner()
                    && type == description.getType()) {
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

    default  String getShortDescription() {
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
