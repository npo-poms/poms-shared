package nl.vpro.domain.media;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public interface  MediaProvider {

    /**
     * Defaulting version of {@link #findByMid(boolean, String)} with first argument {@code true}
     */
    @Nullable
    default <T extends MediaObject> T  findByMid(String mid) {
        return findByMid(true, mid);
    }

    @Nullable
    <T extends MediaObject> T  findByMid(boolean loadDeleted, String mid);

}
