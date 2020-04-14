package nl.vpro.domain.media;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public interface  MediaProvider {

    default <T extends MediaObject> T  findByMid(String mid) {
        return findByMid(mid, true);
    }

    <T extends MediaObject> T  findByMid(String mid, boolean loadDeleted);

}
