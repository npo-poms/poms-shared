package nl.vpro.domain.api.profile;

import java.time.Instant;
import java.util.Map;
import java.util.SortedSet;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface ProfileRepository {

    @Nullable
    default SortedSet<Profile> getProfiles(String name) {
        if (name == null) {
            return null;
        }
        return getProfiles().get(name);
    }

    Map<String, SortedSet<Profile>> getProfiles();


    default  ProfileDefinition<Page> getPageProfileDefinition(String name) {
        if (name == null) {
            return null;
        }
        SortedSet<Profile> definitions = getProfiles(name);
        Profile profile = definitions == null || definitions.isEmpty() ? null : definitions.first();
        return profile != null ? profile.getPageProfile() : null;
    }

    default  ProfileDefinition<MediaObject> getMediaProfileDefinition(String name) {
        SortedSet<Profile> definitions = getProfiles(name);
        Profile profile = definitions == null || definitions.isEmpty() ? null : definitions.first();
        return profile != null ? profile.getMediaProfile() : null;
    }

    default ProfileDefinition<MediaObject> getMediaProfileDefinition(String name, Instant since) {
        throw new UnsupportedOperationException();
    }


}
