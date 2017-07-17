package nl.vpro.domain.subtitles;

import java.time.Duration;
import java.util.Locale;

import javax.persistence.metamodel.SingularAttribute;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class Subtitles_ {


    static SingularAttribute<Subtitles, String> mid;

    static SingularAttribute<Subtitles, SubtitlesType> type;

    static SingularAttribute<Subtitles, Duration> offset;

    static SingularAttribute<Subtitles, Locale> language;

    static SingularAttribute<Subtitles, Integer> cueCount;


}
