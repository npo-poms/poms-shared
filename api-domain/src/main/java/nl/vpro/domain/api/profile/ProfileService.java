package nl.vpro.domain.api.profile;

import java.util.List;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface ProfileService {

    List<Profile> getProfiles();

    Profile getProfile(String name);

    ProfileDefinition<Page> getPageProfileDefinition(String name);

    ProfileDefinition<MediaObject> getMediaProfileDefinition(String name);

}
