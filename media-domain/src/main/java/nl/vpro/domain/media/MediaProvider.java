package nl.vpro.domain.media;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public interface  MediaProvider {

    /**
     *
     */
    default <T extends MediaObject> T  findByMid(String mid) {
        return findByMid(true, mid);
    }

    <T extends MediaObject> T  findByMid(boolean loadDeleted, String mid);

}
