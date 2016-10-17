package nl.vpro.domain.media;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public interface  MediaProvider {

    <T extends MediaObject> T  findByMid(String mid);

}
