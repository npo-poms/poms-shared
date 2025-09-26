package nl.vpro.domain;

import java.util.*;
import java.util.function.Supplier;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.Tag;

/**
 * An object that can be translated. The is valid for the fields of {@link TextualObject}, but also for {@link Tag}, {@link Website}, and {@link SocialRef} references.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 * @param <T> The type of one title
 * @param <D> The type of one description
 * @param <WS> The type of one website object
 * @param <TR> The type of one twitter reference
 * @param <TO> This type itself
 */
public interface LocalizedObject<
    T extends OwnedText,
    D extends OwnedText,
    WS extends Supplier<String> & UpdatableIdentifiable<Long, WS>,
    TR extends Supplier<String>,
    TO extends LocalizedObject<T, D, WS, TR, TO>>
    extends TextualObject<T, D, TO> {


    SortedSet<Tag> getTags();
    void setTags(Set<Tag> tags);
    default TO addTag(Tag tag) {
        getTags().add(tag);
        return self();
    }
    default boolean removeTag(Tag  tag) {
        SortedSet<Tag> tags = getTags();
        return tags != null && tags.remove(tag);
    }

    List<WS> getWebsites();

    TO setWebsites(List<WS> websites);

    default WS getMainWebsite() {
        return getWebsites().stream().findFirst().orElse(null);
    }

    default WS findWebsite(Long id) {
        for (WS website : getWebsites()) {
            if (id.equals(website.getId())) {
                return website;
            }
        }
        return null;
    }

    default WS findWebsite(WS website) {
        List<WS > websites = getWebsites();
        int index = websites.indexOf(website);
        if (index >= 0) {
            return websites.get(index);
        }
        return null;
    }

    default WS getWebsite(final WS website) {
        for (WS existing : getWebsites()) {
            if (existing.equals(website)) {
                return existing;
            }
        }
        return null;
    }

    default void addWebsite(final WS website) {
        if (website != null) {
            getWebsites().remove(website);
            getWebsites().add(website);
        }
    }

    default void addWebsite(int index, final WS website) {
        if (website != null) {
            List<WS> websites = getWebsites();
            websites.remove(website);
            if (index < websites.size()) {
                websites.add(index, website);
            } else {
                websites.add(website);
            }
        }
    }

    default boolean removeWebsite(final Long id) {
        for (Iterator<WS> iterator = getWebsites().iterator(); iterator.hasNext(); ) {
            WS website = iterator.next();
            if (id.equals(website.getId())) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    default boolean removeWebsite(final WS website) {
        return getWebsites().remove(website);
    }

    List<TR> getSocialRefs();

    void setSocialRefs(List<TR> twitterRefs);

    default void addSocialRef(TR ref) {
        List<TR> socialRefs = getSocialRefs();

        if (socialRefs == null) {
            socialRefs = new ArrayList<>();
        }

        if (!socialRefs.contains(ref)) {
            socialRefs.add(ref);
        }
    }


}
