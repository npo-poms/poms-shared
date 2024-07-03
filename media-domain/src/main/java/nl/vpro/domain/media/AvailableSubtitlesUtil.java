package nl.vpro.domain.media;

import nl.vpro.domain.subtitles.Subtitles;

/**
 * @since 8.2
 */
public class AvailableSubtitlesUtil {

    public static  AvailableSubtitles toAvailable(Subtitles subtitles) {
        return AvailableSubtitles.builder()
            .language(subtitles.getLanguage())
            .type(subtitles.getType())
            .workflow(subtitles.getWorkflow())
            .build();
    }
}
