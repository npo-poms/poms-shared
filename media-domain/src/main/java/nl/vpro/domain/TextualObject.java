package nl.vpro.domain;

import java.util.Locale;
import java.util.SortedSet;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.i18n.Locales;

/**
 * Represents an object having titles and descriptions.
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface TextualObject<T extends OwnedText, D extends OwnedText, TO extends TextualObject<T, D, TO>> {

    default Locale getLocale() {
        return Locales.DUTCH;
    }

    boolean hasTitles();
    SortedSet<T> getTitles();
    void setTitles(SortedSet<T> titles);
    TO addTitle(T title);
    boolean removeTitle(T title);

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

    TO addTitle(String title, OwnerType owner, TextualType type);


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
        String string = TextualObjects.get(getTitles(), TextualType.LEXICO);
        if (StringUtils.isEmpty(string)) {
            return getMainTitle();
            //return StringUtils.isNotEmpty(string) ? string : TextUtil.getLexico(getMainTitle(), new Locale("nl", "NL"));
        } else {
            return string;
        }
    }

    /**
     * @since 2.1
     */
    default String getAbbreviatedTitle() {
        return TextualObjects.get(getTitles(), TextualType.ABBREVIATION);
    }


    boolean hasDescriptions();
    SortedSet<D> getDescriptions();
    void setDescriptions(SortedSet<D> descriptions);
    TextualObject addDescription(D description);
    boolean removeDescription(D description);


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

    TO addDescription(String description, OwnerType owner, TextualType type);

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
