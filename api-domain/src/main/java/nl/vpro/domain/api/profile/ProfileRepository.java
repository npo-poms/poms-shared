package nl.vpro.domain.api.profile;

import java.time.Instant;
import java.util.Map;
import java.util.SortedSet;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface ProfileRepository {

    SortedSet<Profile> getProfiles(String name);

    Map<String, SortedSet<Profile>> getProfiles();

    ProfileDefinition<Page> getPageProfileDefinition(String name);

    ProfileDefinition<MediaObject> getMediaProfileDefinition(String name);

    ProfileDefinition<MediaObject> getMediaProfileDefinition(String name, Instant since);


}
