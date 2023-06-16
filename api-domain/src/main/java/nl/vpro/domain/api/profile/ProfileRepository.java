package nl.vpro.domain.api.profile;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface ProfileRepository {

    @Nullable
    default Profile getProfile(String name) {
        if (name == null) {
            return null;
        }
        return getProfiles().get(name);
    }

    Map<String, Profile> getProfiles();


    default  ProfileDefinition<Page> getPageProfileDefinition(String name) {
        if (name == null) {
            return null;
        }
        Profile profile = getProfile(name);
        return profile != null ? profile.getPageProfile() : null;
    }

    default  ProfileDefinition<MediaObject> getMediaProfileDefinition(String name) {
        Profile profile = getProfile(name);
        return profile != null ? profile.getMediaProfile() : null;
    }


}
