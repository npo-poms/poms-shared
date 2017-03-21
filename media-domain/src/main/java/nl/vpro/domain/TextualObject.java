package nl.vpro.domain;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * Represents an object having owned and typed titles and descriptions.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface TextualObject<T extends OwnedText<T>, D extends OwnedText<D>, TO extends TextualObject<T, D, TO>>
    extends TextualObjectUpdate<T, D, TO> {

    TO addTitle(String title, OwnerType owner, TextualType type);

    @Override
    default void setMainTitle(String mainTitle) {
        addTitle(mainTitle, OwnerType.BROADCASTER, TextualType.MAIN);

    }


    default boolean removeTitle(OwnerType owner, TextualType type) {
        if (hasTitles()) {
            return getTitles()
                .removeIf(
                    title ->
                        title.getOwner().equals(owner) && title.getType() == type
                );

        }
        return false;
    }

    default TO removeTitlesForOwner(OwnerType owner) {
        if (hasTitles()) {
            getTitles().removeIf(
                title -> title.getOwner().equals(owner)
            );
        }
        return self();
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

    TO addDescription(String description, OwnerType owner, TextualType type);

    default boolean removeDescription(OwnerType owner, TextualType type) {
        if (hasDescriptions()) {
            return getDescriptions().removeIf(
                description ->
                    description.getOwner().equals(owner) &&
                        description.getType() == type
            );
        }
        return false;
    }


    default TO removeDescriptionsForOwner(OwnerType owner) {
        if (hasDescriptions()) {

            getDescriptions().removeIf(description -> description.getOwner().equals(owner));
        }
        return self();
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
}
