package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
public interface MediaLoader extends MediaProvider {

    @NonNull
    List<MediaObject> loadAll(boolean loadDeleted, List<String> ids);

    default List<MediaObject> loadAll(List<String> ids){
        return loadAll(false, ids);
    }

    default MediaObject load(boolean loadDeleted, String id) {
        return loadAll(loadDeleted, Arrays.asList(id)).get(0);
    }

    default MediaObject load(String id) {
        return load(false, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    default <T extends MediaObject> T findByMid(boolean loadDeleted, String mid) {
        return (T) load(loadDeleted, mid);
    }

}
