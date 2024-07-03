package nl.vpro.domain.subtitles;

import java.util.List;

/**
 * @since 8.2
 */
@FunctionalInterface
public interface SubtitlesProvider {

    List<Subtitles> list(String mid);
}
