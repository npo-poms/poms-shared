package nl.vpro.domain.media;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public interface  MediaProvider extends Function<String,MediaObject> {

    /**
     * Defaulting version of {@link #findByMid(boolean, String)} with first argument {@code true}
     */
    @Nullable
    default <T extends MediaObject> T  findByMid(String mid) {
        return findByMid(true, mid);
    }

    @Nullable
    <T extends MediaObject> T  findByMid(boolean loadDeleted, String mid);

    @Override
    default MediaObject apply(String id) {
        return findByMid(false, id);
    }

}
