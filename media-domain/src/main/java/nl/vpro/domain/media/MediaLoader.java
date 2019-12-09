package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
public interface MediaLoader extends MediaProvider {

    List<MediaObject> loadAll(List<String> ids);

    default MediaObject load(String id) {
        return loadAll(Arrays.asList(id)).get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <T extends MediaObject> T findByMid(String mid) {
        return (T) load(mid);
    }

}
