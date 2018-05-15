package nl.vpro.domain;


import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.util.TriFunction;

/**
 * Represents an object having owned and typed titles and descriptions.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface TextualObject<
    T extends OwnedText,
    D extends OwnedText,
    TO extends TextualObject<T, D, TO>>
    extends TextualObjectUpdate<T, D, TO> {

    OwnerType DEFAULT_OWNER = OwnerType.BROADCASTER;

    /**
     * @since 5.5
     */
    TriFunction<String, OwnerType, TextualType, T> getOwnedTitleCreator();

    @Override
    default BiFunction<String, TextualType, T> getTitleCreator() {
        return (s, t) -> getOwnedTitleCreator().apply(s, DEFAULT_OWNER, t);
    }

    default TO addTitle(String title, @Nonnull  OwnerType owner, @Nonnull  TextualType type) {
        T t = getOwnedTitleCreator().apply(title, owner, type);
        getTitles().add(t);
        return self();
    }

    @Override
    default TO addTitle(String title,  @Nonnull TextualType type) {
        return addTitle(title, DEFAULT_OWNER, type);
    }

    default boolean removeTitle(@Nonnull OwnerType owner, @Nonnull TextualType type) {
        if (hasTitles()) {
            return getTitles()
                .removeIf(
                    title ->
                        title.getOwner().equals(owner) && title.getType() == type
                );

        }
        return false;
    }

    default TO removeTitlesForOwner(@Nonnull OwnerType owner) {
        if (hasTitles()) {
            getTitles().removeIf(
                title -> title.getOwner().equals(owner)
            );
        }
        return self();
    }

    default T findTitle(@Nonnull OwnerType owner, @Nonnull TextualType type) {
        if (hasTitles()) {
            for (T title : getTitles()) {
                if (owner == title.getOwner() && type == title.getType()) {
                    return title;
                }
            }
        }
        return null;
    }


    @Override
    default BiFunction<String, TextualType, D> getDescriptionCreator() {
        return (s, t) -> getOwnedDescriptionCreator().apply(s, DEFAULT_OWNER, t);
    }

    /**
     * @since 5.5
     */
    TriFunction<String, OwnerType, TextualType, D> getOwnedDescriptionCreator();

    default TO addDescription(String description, OwnerType owner, TextualType type) {
        D d = getOwnedDescriptionCreator().apply(description, owner, type);
        getDescriptions().add(d);
        return self();
    }

    @Override
    default TO addDescription(String title, @Nonnull TextualType type) {
        return addDescription(title, DEFAULT_OWNER, type);
    }

    default boolean removeDescription(@Nonnull OwnerType owner, @Nonnull TextualType type) {
        if (hasDescriptions()) {
            return getDescriptions().removeIf(
                description ->
                    description.getOwner().equals(owner) &&
                        description.getType() == type
            );
        }
        return false;
    }


    default TO removeDescriptionsForOwner(@Nonnull OwnerType owner) {
        if (hasDescriptions()) {

            getDescriptions().removeIf(description -> description.getOwner().equals(owner));
        }
        return self();
    }

    default D findDescription(@Nonnull OwnerType owner, @Nonnull TextualType type) {
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
