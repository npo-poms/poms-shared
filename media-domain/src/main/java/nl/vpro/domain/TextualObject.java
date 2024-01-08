package nl.vpro.domain;


import java.util.SortedSet;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.meeuw.functional.TriFunction;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * Represents an object having owned and typed titles and descriptions.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 * @param <T> The type of the title
 * @param <D> The type of the description
 * @param <TO> The type of the object itself
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

    default TO addTitle(@NonNull String title, @NonNull  OwnerType owner, @NonNull  TextualType type) {
        removeTitle(OwnerType.TEMPORARY, type);
        T t = getOwnedTitleCreator().apply(title, owner, type);
        SortedSet<T> titles = getTitles();
        getTitles().add(t);
        return self();
    }

    /**
     * Sets title if already set, otherwise {@link #addTitle(String, OwnerType owner, TextualType)}
     * @since 5.11
     */
    default void setTitle(String title, @NonNull OwnerType owner, @NonNull TextualType type) {
        if (getTitles() != null) {
            for (T t : getTitles()) {
                if (t.getType() == type && t.getOwner() == owner) {
                    t.set(title);
                    return;
                }
            }
        }
        addTitle(title, owner, type);
    }

    @Override
    default TO addTitle(@NonNull String title,  @NonNull TextualType type) {
        return addTitle(title, DEFAULT_OWNER, type);
    }

    default boolean removeTitle(@NonNull OwnerType owner, @NonNull TextualType type) {
        if (hasTitles()) {
            return getTitles()
                .removeIf(
                    title ->
                        title.getOwner().equals(owner) && title.getType() == type
                );

        }
        return false;
    }

    default TO removeTitlesForOwner(@NonNull OwnerType owner) {
        if (hasTitles()) {
            getTitles().removeIf(
                title -> title.getOwner().equals(owner)
            );
        }
        return self();
    }

    default T findTitle(@NonNull OwnerType owner, @NonNull TextualType type) {
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

    default TO addDescription(@Nullable String description, @NonNull OwnerType owner, @NonNull TextualType type) {
        if (StringUtils.isNotEmpty(description)) {
            removeDescription(OwnerType.TEMPORARY, type);
            D d = getOwnedDescriptionCreator().apply(description, owner, type);
            getDescriptions().add(d);
        }

        return self();
    }


    /**
     * @param description the new description to set for given owner/type. If empty or blank, the description will be <em>removed</em>
     * @since 5.11
     */
    default void setDescription(@Nullable String description, @NonNull OwnerType owner, @NonNull TextualType type) {
        if (StringUtils.isBlank(description)) {
            removeDescription(owner, type);
        } else {
            if (getDescriptions() != null) {
                for (D d : getDescriptions()) {
                    if (d.getType() == type && d.getOwner() == owner) {
                        d.set(description);
                        return;
                    }
                }
            }
            addDescription(description, owner, type);
        }
    }

    @Override
    default TO addDescription(@Nullable String description, @NonNull TextualType type) {
        return addDescription(description, DEFAULT_OWNER, type);
    }

    default boolean removeDescription(@NonNull OwnerType owner, @NonNull TextualType type) {
        if (hasDescriptions()) {
            return getDescriptions().removeIf(
                description ->
                    description.getOwner().equals(owner) &&
                        description.getType() == type
            );
        }
        return false;
    }


    default TO removeDescriptionsForOwner(@NonNull OwnerType owner) {
        if (hasDescriptions()) {

            getDescriptions().removeIf(description -> description.getOwner().equals(owner));
        }
        return self();
    }

    default D findDescription(@NonNull OwnerType owner, @NonNull TextualType type) {
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
