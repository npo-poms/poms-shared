package nl.vpro.domain.media;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public interface  MediaProvider {

    /**
     * Defaulting version of {@link #findByMid(boolean, String)} with first argument {@code true}
     */
    default <T extends MediaObject> T  findByMid(String mid) {
        return findByMid(true, mid);
    }

    <T extends MediaObject> T  findByMid(boolean loadDeleted, String mid);

}
