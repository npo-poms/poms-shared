package nl.vpro.domain.media;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public interface SubMediaType {

    MediaType getMediaType();

    String getUrnPrefix();

    String name();

    default boolean canContainEpisodes() {
        return false;
    }

    default boolean hasEpisodeOf() {
        return false;
    }

    default boolean canHaveScheduleEvents() {
        return false;
    }

    boolean canBeCreatedByNormalUsers();

}
