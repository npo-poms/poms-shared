package nl.vpro.domain.api.media;

import java.util.List;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaProvider;

/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
public interface MediaLoader extends MediaProvider, MediaRedirector {

    List<MediaObject> loadAll(List<String> ids);

    MediaObject load(String id);

    @SuppressWarnings("unchecked")
    @Override
    default <T extends MediaObject> T findByMid(String mid) {
        return (T) load(mid);
    }

}
